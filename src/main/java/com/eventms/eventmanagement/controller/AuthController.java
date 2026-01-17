package com.eventms.eventmanagement.controller;

import com.eventms.eventmanagement.dto.AuthResponse;
import com.eventms.eventmanagement.dto.LoginRequest;
import com.eventms.eventmanagement.dto.RegisterRequest;
import com.eventms.eventmanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
