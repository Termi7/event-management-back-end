
package com.eventms.eventmanagement.dto;

import com.eventms.eventmanagement.model.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationDto(
        Long id,
        Long eventId,
        String eventTitle,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        RegistrationStatus status,
        LocalDateTime registeredAt
) {}
