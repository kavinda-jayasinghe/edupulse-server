package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.service.RankingService;
import com.info_labs.edupulse.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final RankingService rankingService;

    // ── Overview ──────────────────────────────────────────────

    @GetMapping("/{teacherId}/overview")
    public ResponseEntity<?> getOverview(@PathVariable Integer teacherId) {
        return ResponseEntity.ok(teacherService.getOverview(teacherId));
    }

    @GetMapping("/{teacherId}/rankings/{classId}")
    public ResponseEntity<?> getRankings(@PathVariable Integer teacherId,
                                         @PathVariable Integer classId) {
        return ResponseEntity.ok(rankingService.getRankings(classId));
    }

    // ── Class CRUD ────────────────────────────────────────────

    @PostMapping("/{teacherId}/classes")
    public ResponseEntity<?> createClass(@PathVariable Integer teacherId,
                                          @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(teacherService.createClass(
            teacherId,
            body.get("name"),
            body.get("classCode"),
            body.get("subject")
        ));
    }

    @GetMapping("/{teacherId}/classes/{classId}")
    public ResponseEntity<?> getClassDetail(@PathVariable Integer teacherId,
                                             @PathVariable Integer classId) {
        return ResponseEntity.ok(teacherService.getClassDetail(teacherId, classId));
    }

    @PutMapping("/{teacherId}/classes/{classId}")
    public ResponseEntity<?> updateClass(@PathVariable Integer teacherId,
                                          @PathVariable Integer classId,
                                          @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(teacherService.updateClass(
            teacherId, classId,
            body.get("name"),
            body.get("classCode"),
            body.get("subject")
        ));
    }

    @DeleteMapping("/{teacherId}/classes/{classId}")
    public ResponseEntity<?> deleteClass(@PathVariable Integer teacherId,
                                          @PathVariable Integer classId) {
        teacherService.deleteClass(teacherId, classId);
        return ResponseEntity.noContent().build();
    }

    // ── Student Management ────────────────────────────────────

    @PostMapping("/{teacherId}/classes/{classId}/students")
    public ResponseEntity<?> addStudent(@PathVariable Integer teacherId,
                                         @PathVariable Integer classId,
                                         @RequestBody Map<String, String> body) {
        teacherService.addStudentToClass(teacherId, classId, body.get("mobile"));
        return ResponseEntity.ok(Map.of("message", "Student added successfully"));
    }

    @DeleteMapping("/{teacherId}/classes/{classId}/students/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable Integer teacherId,
                                            @PathVariable Integer classId,
                                            @PathVariable Integer studentId) {
        teacherService.removeStudentFromClass(teacherId, classId, studentId);
        return ResponseEntity.noContent().build();
    }

    // ── Assignment Management ─────────────────────────────────

    @PostMapping("/{teacherId}/classes/{classId}/assignments")
    public ResponseEntity<?> createAssignment(@PathVariable Integer teacherId,
                                               @PathVariable Integer classId,
                                               @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(teacherService.createAssignment(
            teacherId, classId,
            body.get("title"),
            body.get("description"),
            body.get("dueDate")
        ));
    }

    @PutMapping("/{teacherId}/classes/{classId}/assignments/{assignmentId}")
    public ResponseEntity<?> updateAssignment(@PathVariable Integer teacherId,
                                               @PathVariable Integer classId,
                                               @PathVariable Integer assignmentId,
                                               @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(teacherService.updateAssignment(
            teacherId, classId, assignmentId,
            body.get("title"),
            body.get("description"),
            body.get("dueDate")
        ));
    }

    @DeleteMapping("/{teacherId}/classes/{classId}/assignments/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Integer teacherId,
                                               @PathVariable Integer classId,
                                               @PathVariable Integer assignmentId) {
        teacherService.deleteAssignment(teacherId, classId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    // ── Assignment File Management ─────────────────────────────

    @PostMapping(value = "/{teacherId}/classes/{classId}/assignments/{assignmentId}/files",
                 consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(@PathVariable Integer teacherId,
                                          @PathVariable Integer classId,
                                          @PathVariable Integer assignmentId,
                                          @RequestParam("files") java.util.List<org.springframework.web.multipart.MultipartFile> files) {
        return ResponseEntity.ok(teacherService.uploadAssignmentFiles(teacherId, classId, assignmentId, files));
    }

    @DeleteMapping("/{teacherId}/classes/{classId}/assignments/{assignmentId}/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer teacherId,
                                         @PathVariable Integer classId,
                                         @PathVariable Integer assignmentId,
                                         @PathVariable Integer fileId) {
        teacherService.deleteAssignmentFile(teacherId, classId, assignmentId, fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teacherId}/classes/{classId}/assignments/{assignmentId}/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer teacherId,
                                           @PathVariable Integer classId,
                                           @PathVariable Integer assignmentId,
                                           @PathVariable Integer fileId) {
        return teacherService.getAssignmentFileContent(teacherId, classId, assignmentId, fileId);
    }
}
