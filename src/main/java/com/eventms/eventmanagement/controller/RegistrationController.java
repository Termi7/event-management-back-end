package com.eventms.eventmanagement.controller;

import com.eventms.eventmanagement.dto.RegistrationDto;
import com.eventms.eventmanagement.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/events/{id}/register")
    public ResponseEntity<RegistrationDto> register(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.register(id));
    }


    // DELETE /api/registrations/{id}
    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        registrationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/users/me/registrations
    @GetMapping("/users/me/registrations")
    public ResponseEntity<List<RegistrationDto>> myRegistrations() {
        return ResponseEntity.ok(registrationService.myRegistrations());
    }
}
