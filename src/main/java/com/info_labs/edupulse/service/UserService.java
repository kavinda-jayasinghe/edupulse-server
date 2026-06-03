package com.info_labs.edupulse.service;

import com.info_labs.edupulse.config.CommonException;
import com.info_labs.edupulse.dto.ChangePasswordRequest;
import com.info_labs.edupulse.dto.JoinClassResponseDto;
import com.info_labs.edupulse.dto.ProfileUpdateDto;
import com.info_labs.edupulse.dto.UpdateProfileRequest;
import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.StudentExam;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.repository.ClassRepository;
import com.info_labs.edupulse.repository.StudentExamRepository;
import com.info_labs.edupulse.repository.UserRepository;
import com.info_labs.edupulse.utils.ProfileType;
import com.info_labs.edupulse.utils.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository        userRepository;
    private final StudentExamRepository studentExamRepository;
    private final RankingService        rankingService;
    private final ClassRepository       classRepository;
    private final PasswordEncoder          passwordEncoder;

    @Transactional
    public ProfileUpdateDto updateProfile(Integer userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String newMobile = req.mobile().trim();
        if (!user.getMobile().equals(newMobile) && userRepository.existsByMobile(newMobile)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already in use");
        }

        user.setName(req.name().trim());
        user.setMobile(newMobile);
        userRepository.save(user);
        return new ProfileUpdateDto(user.getId(), user.getName(), user.getMobile());
    }

    @Transactional(readOnly = true)
    public void verifyPassword(Integer userId, String password) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (req.newPassword() == null || req.newPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 6 characters");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public JoinClassResponseDto joinClass(Integer studentId, String classCode) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ClassEntity cls = classRepository.findByClassCode(classCode.trim().toUpperCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No class found with code \"" + classCode.toUpperCase() + "\""));

        if (student.getClasses().contains(cls)) {
            throw new CommonException(ResponseCode.ALREADY_ENROLLED);}

        student.getClasses().add(cls);
        userRepository.save(student);

        return new JoinClassResponseDto(cls.getName(), "Successfully joined \"" + cls.getName() + "\"");
    }

    public Map<String, Object> getDashboard(Integer studentId) {
        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<StudentExam> attempts = studentExamRepository.findByStudent(user);
        int totalScore = attempts.stream().mapToInt(StudentExam::getScore).sum();
        int totalDots  = Math.min(
            attempts.stream().mapToInt(se ->
                Math.round((float) se.getScore() / se.getExam().getTotal() * 100)
            ).sum(), 500
        );

        // Best rank across all enrolled classes
        Integer bestRank = null;
        for (ClassEntity cls : user.getClasses()) {
            List<Map<String, Object>> rankings = rankingService.getRankings(cls.getId());
            Integer rank = rankings.stream()
                .filter(r -> user.getId().equals(r.get("id")))
                .map(r -> (Integer) r.get("rank"))
                .findFirst().orElse(null);
            if (rank != null && (bestRank == null || rank < bestRank)) bestRank = rank;
        }

        // Primary class info (first enrolled class, sorted by id)
        ClassEntity primaryClass = user.getClasses().stream()
            .min(Comparator.comparing(ClassEntity::getId))
            .orElse(null);

        List<Map<String, Object>> recentExams = new ArrayList<>();
        int start = Math.max(0, attempts.size() - 5);
        List<StudentExam> recent = new ArrayList<>(attempts.subList(start, attempts.size()));
        Collections.reverse(recent);
        for (StudentExam se : recent) {
            long examRank = studentExamRepository.countByExamAndScoreGreaterThan(se.getExam(), se.getScore()) + 1;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id",    se.getId());
            entry.put("title", se.getExam().getTitle());
            entry.put("score", se.getScore());
            entry.put("total", se.getExam().getTotal());
            entry.put("rank",  examRank);
            entry.put("date",  se.getSubmittedAt());
            recentExams.add(entry);
        }

        List<Map<String, Object>> classesList = user.getClasses().stream()
            .sorted(Comparator.comparing(ClassEntity::getId))
            .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
            .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("student", Map.of(
            "id",         user.getId(),
            "name",       user.getName(),
            "class_name", primaryClass != null ? primaryClass.getName() : "",
            "classes",    classesList
        ));
        result.put("totalExams",  attempts.size());
        result.put("totalScore",  totalScore);
        result.put("currentRank", bestRank);
        result.put("totalDots",   totalDots);
        result.put("recentExams", recentExams);
        return result;
    }

    public Map<String, Object> getProfile(Integer studentId) {
        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<StudentExam> attempts = studentExamRepository.findByStudent(user);
        int totalScore = attempts.stream().mapToInt(StudentExam::getScore).sum();

        Integer bestRank = null;
        for (ClassEntity cls : user.getClasses()) {
            List<Map<String, Object>> rankings = rankingService.getRankings(cls.getId());
            Integer rank = rankings.stream()
                .filter(r -> user.getId().equals(r.get("id")))
                .map(r -> (Integer) r.get("rank"))
                .findFirst().orElse(null);
            if (rank != null && (bestRank == null || rank < bestRank)) bestRank = rank;
        }

        ClassEntity primaryClass = user.getClasses().stream()
            .min(Comparator.comparing(ClassEntity::getId))
            .orElse(null);

        List<Map<String, Object>> classesList = user.getClasses().stream()
            .sorted(Comparator.comparing(ClassEntity::getId))
            .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
            .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",          user.getId());
        result.put("name",        user.getName());
        result.put("mobile",      user.getMobile());
        result.put("profileType", user.getProfileType() != null ? user.getProfileType().name() : "STUDENT");
        result.put("class_id",    primaryClass != null ? primaryClass.getId() : null);
        result.put("class_name",  primaryClass != null ? primaryClass.getName() : "");
        result.put("classes",     classesList);
        result.put("totalExams",  attempts.size());
        result.put("totalScore",  totalScore);
        result.put("currentRank", bestRank);
        return result;
    }
}
