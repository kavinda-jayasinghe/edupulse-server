package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{studentId}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Integer studentId) {
        return ResponseEntity.ok(userService.getDashboard(studentId));
    }

    @GetMapping("/{studentId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Integer studentId) {
        return ResponseEntity.ok(userService.getProfile(studentId));
    }
}
