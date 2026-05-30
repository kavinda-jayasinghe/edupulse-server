package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.AdminService;
import com.info_labs.edupulse.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService   adminService;
    private final RankingService rankingService;

    // ── Dashboard ─────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/class-overview")
    public ResponseEntity<?> getClassOverview() {
        return ResponseEntity.ok(adminService.getClassOverview());
    }

    @GetMapping("/rankings/{classId}")
    public ResponseEntity<?> getRankings(@PathVariable Integer classId) {
        return ResponseEntity.ok(rankingService.getRankings(classId));
    }

    // ── User listing ──────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllUsersPaged(page, size));
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUser(@RequestParam String mobile) {
        return ResponseEntity.ok(adminService.searchByMobile(mobile));
    }

    // ── CRUD operations ───────────────────────────────────────

    @PutMapping("/users/{id}/profile-type")
    public ResponseEntity<?> changeProfileType(@PathVariable Integer id,
                                               @RequestBody Map<String, String> body) {
        String profileType = body.get("profileType");
        if (profileType == null || profileType.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "profileType is required"));
        }
        return ResponseEntity.ok(adminService.changeProfileType(id, profileType));
    }

    @PutMapping("/users/{id}/toggle-enabled")
    public ResponseEntity<?> toggleEnabled(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.toggleEnabled(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
