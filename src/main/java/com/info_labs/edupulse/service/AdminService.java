package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.repository.*;
import com.info_labs.edupulse.utils.ProfileType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository        userRepository;
    private final ClassRepository       classRepository;
    private final ExamRepository        examRepository;
    private final StudentExamRepository studentExamRepository;
    private final NotificationRepository notificationRepository;

    // ── Dashboard data ────────────────────────────────────────

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents",    userRepository.countByProfileType(ProfileType.STUDENT));
        stats.put("totalClasses",     classRepository.count());
        stats.put("totalExams",       examRepository.count());
        stats.put("totalSubmissions", studentExamRepository.count());
        return stats;
    }

    public List<Map<String, Object>> getClassOverview() {
        List<ClassEntity> classes = classRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ClassEntity cls : classes) {
            long studentCount = userRepository.findByClassesContaining(cls).stream()
                .filter(u -> u.getProfileType() == ProfileType.STUDENT)
                .count();

            List<Object[]> statRows = studentExamRepository.classExamStats(cls);
            Object[] examStats  = statRows.isEmpty() ? new Object[]{0L, null} : statRows.get(0);
            long submissions = examStats[0] != null ? ((Number) examStats[0]).longValue() : 0L;
            long avgScore    = examStats[1] != null ? Math.round(((Number) examStats[1]).doubleValue()) : 0L;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id",           cls.getId());
            entry.put("name",         cls.getName());
            entry.put("studentCount", studentCount);
            entry.put("submissions",  submissions);
            entry.put("avgScore",     avgScore);
            result.add(entry);
        }

        return result;
    }

    // ── User listing ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsersPaged(int page, int size) {
        Page<User> usersPage = userRepository.findByProfileTypeNot(
            ProfileType.ADMIN,
            PageRequest.of(page, size, Sort.by("id").ascending())
        );
        List<Map<String, Object>> content = usersPage.getContent().stream()
            .map(this::toUserMap)
            .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content",       content);
        result.put("totalElements", usersPage.getTotalElements());
        result.put("totalPages",    usersPage.getTotalPages());
        result.put("page",          usersPage.getNumber());
        result.put("size",          usersPage.getSize());
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> searchByMobile(String mobile) {
        User u = userRepository.findByMobile(mobile)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with that mobile number"));
        if (u.getProfileType() == ProfileType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be managed here");
        }
        return toUserMap(u);
    }

    // ── CRUD operations ───────────────────────────────────────

    public Map<String, Object> changeProfileType(Integer userId, String profileTypeName) {
        User u = getManageableUser(userId);

        ProfileType newType;
        try {
            newType = ProfileType.valueOf(profileTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid profile type");
        }
        if (newType == ProfileType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot assign ADMIN role");
        }

        u.setProfileType(newType);
        userRepository.save(u);
        return Map.of("message", "Profile type updated to " + newType.name(), "profileType", newType.name());
    }

    public Map<String, Object> toggleEnabled(Integer userId) {
        User u = getManageableUser(userId);
        u.setEnabled(!u.isEnabled());
        userRepository.save(u);
        String msg = u.isEnabled() ? "Account enabled" : "Account disabled";
        return Map.of("message", msg, "enabled", u.isEnabled());
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User u = getManageableUser(userId);
        studentExamRepository.deleteByStudent(u);
        notificationRepository.deleteByUser(u);
        userRepository.delete(u);
    }

    // ── Helpers ───────────────────────────────────────────────

    private User getManageableUser(Integer userId) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (u.getProfileType() == ProfileType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be modified");
        }
        return u;
    }

    private Map<String, Object> toUserMap(User u) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id",          u.getId());
        entry.put("name",        u.getName());
        entry.put("mobile",      u.getMobile());
        entry.put("profileType", u.getProfileType().name());
        entry.put("enabled",     u.isEnabled());
        entry.put("classes", u.getClasses().stream()
            .sorted(Comparator.comparing(ClassEntity::getId))
            .map(c -> Map.of("id", c.getId(), "name", c.getName()))
            .collect(Collectors.toList()));
        return entry;
    }
}
