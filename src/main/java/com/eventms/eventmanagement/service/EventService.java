package com.eventms.eventmanagement.service;

import com.eventms.eventmanagement.model.Event;
import com.eventms.eventmanagement.model.User;
import com.eventms.eventmanagement.repository.EventRepository;
import com.eventms.eventmanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @Transactional
    public Event create(Event event) {
        User user = getCurrentUser();
        event.setId(null); // ensure a new record
        event.setCreatedBy(user);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Transactional
    public Event update(Long id, Event updated) {
        Event existing = getById(id);
        User user = getCurrentUser();
        if (existing.getCreatedBy() == null) {
            existing.setCreatedBy(user); // claim orphaned legacy events
        } else if (!existing.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only update your own events");
        }
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setStartAt(updated.getStartAt());
        existing.setEndAt(updated.getEndAt());
        existing.setCapacity(updated.getCapacity());
        return eventRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Event existing = getById(id);
        User user = getCurrentUser();
        if (existing.getCreatedBy() == null) {
            existing.setCreatedBy(user); // claim orphaned legacy events
        } else if (!existing.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own events");
        }
        eventRepository.delete(existing);
    }
}
