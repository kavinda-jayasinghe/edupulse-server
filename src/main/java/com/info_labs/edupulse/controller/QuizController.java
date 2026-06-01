package com.info_labs.edupulse.controller;

import com.info_labs.edupulse.dto.McqAnswerDto;
import com.info_labs.edupulse.dto.QuizResponseDto;
import com.info_labs.edupulse.dto.UpdateQuizRequest;
import com.info_labs.edupulse.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuizResponseDto> createQuiz(
            @RequestParam("teacherId")                              Integer       teacherId,
            @RequestParam("classId")                                Integer       classId,
            @RequestParam("paper")                                  MultipartFile paper,
            @RequestParam("excel")                                  MultipartFile excel,
            @RequestParam("title")                                  String        title,
            @RequestParam(value = "instruction",  required = false) String        instruction,
            @RequestParam("timeDuration")                           Integer       timeDuration,
            @RequestParam(value = "dueDate",      required = false) String        dueDate
    ) throws IOException {
        return ResponseEntity.ok(quizService.createQuiz(teacherId, classId, paper, excel, title, instruction, timeDuration, dueDate));
    }

    @GetMapping("/{quizId}/answers")
    public ResponseEntity<List<McqAnswerDto>> getAnswers(@PathVariable Integer quizId) {
        return ResponseEntity.ok(quizService.getAnswers(quizId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<QuizResponseDto>> getByClass(@PathVariable Integer classId) {
        return ResponseEntity.ok(quizService.getQuizzesByClass(classId));
    }

    @GetMapping("/{quizId}/paper")
    public ResponseEntity<byte[]> getPaper(@PathVariable Integer quizId) {
        return quizService.getPaperFile(quizId);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponseDto> updateQuiz(@PathVariable Integer quizId,
                                                       @RequestParam Integer teacherId,
                                                       @RequestBody UpdateQuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, teacherId, request));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer quizId,
                                            @RequestParam Integer teacherId) {
        quizService.deleteQuiz(quizId, teacherId);
        return ResponseEntity.noContent().build();
    }
}
