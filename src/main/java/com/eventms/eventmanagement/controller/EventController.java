package com.eventms.eventmanagement.controller;

import com.eventms.eventmanagement.model.Event;
import com.eventms.eventmanagement.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    // public routes

    @GetMapping
    public List<Event> listEvents() {
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getById(id);
    }

    // admin routes

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.create(event);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.update(id, event);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }
}
