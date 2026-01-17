package com.eventms.eventmanagement.service;

import com.eventms.eventmanagement.dto.RegistrationDto;
import com.eventms.eventmanagement.model.*;
import com.eventms.eventmanagement.notification.EmailService;
import com.eventms.eventmanagement.repository.EventRepository;
import com.eventms.eventmanagement.repository.RegistrationRepository;
import com.eventms.eventmanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Assumption (common): JWT auth sets Authentication name to user email.
     * If your JwtAuthFilter sets it differently, tell me and I'll adjust.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // comes from JWT
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private RegistrationDto toDto(Registration r) {
        Event e = r.getEvent();
        return new RegistrationDto(
                r.getId(),
                e.getId(),
                e.getTitle(),
                e.getLocation(),
                e.getStartAt(),
                e.getEndAt(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }

    /**
     * POST /api/events/{id}/register
     * Rules:
     * - reject if already registered (not canceled)
     * - check capacity
     * - if full: WAITLIST (or change to reject if you prefer)
     */
    @Transactional
    public RegistrationDto register(Long eventId) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // already registered?
        registrationRepository.findByEvent_IdAndUser_Id(eventId, user.getId()).ifPresent(existing -> {
            if (existing.getStatus() != RegistrationStatus.CANCELED) {
                throw new IllegalStateException("Already registered for this event");
            }
        });

        long registeredCount = registrationRepository.countByEvent_IdAndStatus(eventId, RegistrationStatus.REGISTERED);
        int capacity = (event.getCapacity() == null) ? 0 : event.getCapacity();

        RegistrationStatus status;
        if (capacity > 0 && registeredCount >= capacity) {
            status = RegistrationStatus.WAITLIST; // or throw to reject instead
        } else {
            status = RegistrationStatus.REGISTERED;
        }

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setUser(user);
        reg.setStatus(status);

        Registration saved = registrationRepository.save(reg);

        // Send email only for successful registration (not waitlist)
        if (status == RegistrationStatus.REGISTERED) {
            String subject = "Event registration confirmed";

            String dateText = "TBD";
            if (event.getStartAt() != null) {
                dateText = event.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }

            String body = "You registered for \"" + event.getTitle() + "\" on " + dateText + ".";
            emailService.sendTextEmail(user.getEmail(), subject, body);
        }

        return toDto(saved);
    }

    /**
     * DELETE /api/registrations/{id}
     * Only owner can cancel.
     */
    @Transactional
    public void cancel(Long registrationId) {
        User user = getCurrentUser();

        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (!reg.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Not allowed to cancel this registration");
        }

        reg.setStatus(RegistrationStatus.CANCELED);
        registrationRepository.save(reg);
    }

    /**
     * GET /api/users/me/registrations
     */
    @Transactional(readOnly = true)
    public List<RegistrationDto> myRegistrations() {
        User user = getCurrentUser();

        return registrationRepository
                .findByUser_IdAndStatusNotOrderByCreatedAtDesc(user.getId(), RegistrationStatus.CANCELED)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
