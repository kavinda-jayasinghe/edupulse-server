package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getRankings(@PathVariable Integer classId) {
        return ResponseEntity.ok(rankingService.getRankings(classId));
    }
}
