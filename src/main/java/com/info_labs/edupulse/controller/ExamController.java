package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentExams(@PathVariable Integer studentId) {
        return ResponseEntity.ok(examService.getStudentExams(studentId));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<?> getExam(@PathVariable Integer examId) {
        return ResponseEntity.ok(examService.getExam(examId));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody Map<String, Object> body) {
        Integer examId    = ((Number) body.get("examId")).intValue();
        Integer studentId = ((Number) body.get("studentId")).intValue();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        return ResponseEntity.ok(examService.submitExam(examId, studentId, answers));
    }
}
