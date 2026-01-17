package com.eventms.eventmanagement.repository;

import com.eventms.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
