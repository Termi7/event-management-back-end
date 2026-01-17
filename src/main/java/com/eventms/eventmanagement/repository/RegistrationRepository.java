package com.eventms.eventmanagement.repository;

import com.eventms.eventmanagement.model.Registration;
import com.eventms.eventmanagement.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByEvent_IdAndUser_Id(Long eventId, Long userId);

    long countByEvent_IdAndStatus(Long eventId, RegistrationStatus status);

    List<Registration> findByUser_IdAndStatusNotOrderByCreatedAtDesc(Long userId, RegistrationStatus status);
}
