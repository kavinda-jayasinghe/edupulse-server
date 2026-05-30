package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.*;
import com.info_labs.edupulse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository         examRepository;
    private final QuestionRepository     questionRepository;
    private final StudentExamRepository  studentExamRepository;
    private final UserRepository         userRepository;
    private final NotificationRepository notificationRepository;

    public List<Map<String, Object>> getStudentExams(Integer studentId) {
        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getClasses().isEmpty()) return List.of();

        String today = LocalDate.now().toString();
        // Exams from ALL enrolled classes
        List<Exam> exams = examRepository.findByClassEntityInOrderByDate(user.getClasses());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Exam exam : exams) {
            Optional<StudentExam> attempt = studentExamRepository.findByStudentAndExam(user, exam);
            String status;
            if (attempt.isPresent())                          status = "completed";
            else if (exam.getDate().compareTo(today) > 0)    status = "upcoming";
            else                                              status = "available";

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id",       exam.getId());
            entry.put("title",    exam.getTitle());
            entry.put("class",    exam.getClassEntity().getName());
            entry.put("status",   status);
            entry.put("score",    attempt.map(StudentExam::getScore).orElse(null));
            entry.put("total",    exam.getTotal());
            entry.put("date",     exam.getDate());
            result.add(entry);
        }
        return result;
    }

    public Map<String, Object> getExam(Integer examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));

        List<Question> questions = questionRepository.findByExamOrderByNumber(exam);
        List<Map<String, Object>> qList = new ArrayList<>();
        for (Question q : questions) {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id",      q.getId());
            qMap.put("number",  q.getNumber());
            qMap.put("text",    q.getText());
            qMap.put("options", q.getOptions());
            qList.add(qMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",        exam.getId());
        result.put("title",     exam.getTitle());
        result.put("total",     exam.getTotal());
        result.put("class_id",  exam.getClassEntity().getId());
        result.put("date",      exam.getDate());
        result.put("questions", qList);
        return result;
    }

    public Map<String, Object> submitExam(Integer examId, Integer studentId, List<Map<String, Object>> answers) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (studentExamRepository.existsByStudentAndExam(user, exam)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Exam already submitted");
        }

        List<Question> examQuestions = questionRepository.findByExamOrderByNumber(exam);
        int totalQ        = examQuestions.size();
        int correctCount  = 0;

        if (totalQ > 0 && answers != null && !answers.isEmpty()) {
            Map<Integer, Integer> correctMap = new HashMap<>();
            for (Question q : examQuestions) correctMap.put(q.getId(), q.getCorrectAnswer());
            for (Map<String, Object> ans : answers) {
                Integer qId  = ((Number) ans.get("questionId")).intValue();
                Object  sel  = ans.get("selectedAnswer");
                if (sel == null) continue;
                Integer correct = correctMap.get(qId);
                if (correct != null && Integer.parseInt(sel.toString()) == correct) correctCount++;
            }
        } else {
            correctCount = (int) Math.floor(totalQ * (0.5 + Math.random() * 0.4));
        }

        int score = totalQ > 0
            ? Math.round((float) correctCount / totalQ * exam.getTotal())
            : (int) Math.floor(Math.random() * exam.getTotal() * 0.4 + exam.getTotal() * 0.5);

        StudentExam record = new StudentExam();
        record.setStudent(user);
        record.setExam(exam);
        record.setScore(score);
        record.setSubmittedAt(LocalDate.now().toString());
        studentExamRepository.save(record);

        Notification notif = new Notification();
        notif.setUser(user);
        notif.setMessage("Your score for " + exam.getTitle() + " has been posted: " + score + "/" + exam.getTotal());
        notif.setIsRead(false);
        notif.setDate(LocalDate.now().toString());
        notificationRepository.save(notif);

        return Map.of(
            "message",        "Exam submitted successfully",
            "score",          score,
            "total",          exam.getTotal(),
            "correctCount",   correctCount,
            "totalQuestions", totalQ
        );
    }
}
