package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.dto.ChangePasswordRequest;
import com.info_labs.edupulse.dto.JoinClassRequest;
import com.info_labs.edupulse.dto.JoinClassResponseDto;
import com.info_labs.edupulse.dto.ProfileUpdateDto;
import com.info_labs.edupulse.dto.UpdateProfileRequest;
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

    @PostMapping("/{studentId}/join-class")
    public ResponseEntity<JoinClassResponseDto> joinClass(@PathVariable Integer studentId,
                                                           @RequestBody JoinClassRequest request) {
        return ResponseEntity.ok(userService.joinClass(studentId, request.classCode()));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<ProfileUpdateDto> updateProfile(@PathVariable Integer userId,
                                                           @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @PostMapping("/{userId}/verify-password")
    public ResponseEntity<Void> verifyPassword(@PathVariable Integer userId,
                                                @RequestBody java.util.Map<String, String> body) {
        userService.verifyPassword(userId, body.get("password"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Integer userId,
                                                @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }
}
