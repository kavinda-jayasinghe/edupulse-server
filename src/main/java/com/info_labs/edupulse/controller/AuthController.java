package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses() {
        return ResponseEntity.ok(authService.getClasses());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String mobile   = body.get("mobile");
        String password = body.get("password");
        if (mobile == null || mobile.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mobile number and password are required"));
        }
        return ResponseEntity.ok(authService.login(mobile, password));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name     = body.get("name");
        String mobile   = body.get("mobile");
        String password = body.get("password");
        if (name == null || name.isBlank() || mobile == null || mobile.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Name, mobile number and password are required"));
        }
        authService.register(name, mobile, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Registration successful. You can now log in."));
    }
}
