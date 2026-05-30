package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.Assignment;
import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.repository.*;
import com.info_labs.edupulse.utils.ProfileType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository        userRepository;
    private final ClassRepository       classRepository;
    private final StudentExamRepository studentExamRepository;
    private final AssignmentRepository  assignmentRepository;

    // ── Overview ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getOverview(Integer teacherId) {
        User teacher = getVerifiedTeacher(teacherId);

        long totalStudents    = 0;
        long totalSubmissions = 0;
        List<Map<String, Object>> classData = new ArrayList<>();

        List<ClassEntity> classes = teacher.getClasses().stream()
            .sorted(Comparator.comparing(ClassEntity::getId))
            .collect(Collectors.toList());

        for (ClassEntity cls : classes) {
            long studentCount = userRepository.findByClassesContaining(cls).stream()
                .filter(u -> u.getProfileType() == ProfileType.STUDENT)
                .count();

            Object[] examStats  = studentExamRepository.classExamStats(cls);
            long submissions    = examStats[0] != null ? ((Number) examStats[0]).longValue() : 0L;
            long avgScore       = examStats[1] != null ? Math.round(((Number) examStats[1]).doubleValue()) : 0L;

            totalStudents    += studentCount;
            totalSubmissions += submissions;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id",           cls.getId());
            entry.put("name",         cls.getName());
            entry.put("classCode",    cls.getClassCode() != null ? cls.getClassCode() : "");
            entry.put("subject",      cls.getSubject()   != null ? cls.getSubject()   : "");
            entry.put("studentCount", studentCount);
            entry.put("submissions",  submissions);
            entry.put("avgScore",     avgScore);
            classData.add(entry);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("teacher",          Map.of("id", teacher.getId(), "name", teacher.getName()));
        result.put("totalClasses",     classes.size());
        result.put("totalStudents",    totalStudents);
        result.put("totalSubmissions", totalSubmissions);
        result.put("classes",          classData);
        return result;
    }

    // ── Class CRUD ────────────────────────────────────────────

    @Transactional
    public Map<String, Object> createClass(Integer teacherId, String name, String classCode, String subject) {
        User teacher = getVerifiedTeacher(teacherId);

        String code = classCode != null ? classCode.trim().toUpperCase() : null;
        if (code != null && !code.isEmpty() && classRepository.existsByClassCode(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Class code already in use. Please choose another.");
        }

        ClassEntity cls = new ClassEntity();
        cls.setName(name.trim());
        cls.setClassCode(code != null && !code.isEmpty() ? code : null);
        cls.setSubject(subject != null && !subject.isBlank() ? subject.trim() : null);
        classRepository.save(cls);

        teacher.getClasses().add(cls);
        userRepository.save(teacher);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",        cls.getId());
        result.put("name",      cls.getName());
        result.put("classCode", cls.getClassCode() != null ? cls.getClassCode() : "");
        result.put("subject",   cls.getSubject()   != null ? cls.getSubject()   : "");
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getClassDetail(Integer teacherId, Integer classId) {
        User teacher = getVerifiedTeacher(teacherId);
        ClassEntity cls = getTeacherClass(teacher, classId);

        List<Map<String, Object>> students = userRepository.findByClassesContaining(cls)
            .stream()
            .filter(u -> u.getProfileType() == ProfileType.STUDENT)
            .map(u -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",     u.getId());
                m.put("name",   u.getName());
                m.put("mobile", u.getMobile());
                return m;
            })
            .collect(Collectors.toList());

        List<Map<String, Object>> assignments = assignmentRepository
            .findByClassEntityIdOrderByIdDesc(classId)
            .stream()
            .map(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",          a.getId());
                m.put("title",       a.getTitle());
                m.put("description", a.getDescription() != null ? a.getDescription() : "");
                m.put("dueDate",     a.getDueDate()     != null ? a.getDueDate()     : "");
                m.put("createdAt",   a.getCreatedAt());
                return m;
            })
            .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",          cls.getId());
        result.put("name",        cls.getName());
        result.put("classCode",   cls.getClassCode() != null ? cls.getClassCode() : "");
        result.put("subject",     cls.getSubject()   != null ? cls.getSubject()   : "");
        result.put("students",    students);
        result.put("assignments", assignments);
        return result;
    }

    // ── Student Management ────────────────────────────────────

    @Transactional
    public void addStudentToClass(Integer teacherId, Integer classId, String mobile) {
        User teacher = getVerifiedTeacher(teacherId);
        ClassEntity cls = getTeacherClass(teacher, classId);

        User student = userRepository.findByMobile(mobile)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with mobile: " + mobile));

        if (student.getProfileType() != ProfileType.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That account is not a student");
        }
        if (student.getClasses().contains(cls)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student is already enrolled in this class");
        }

        student.getClasses().add(cls);
        userRepository.save(student);
    }

    @Transactional
    public void removeStudentFromClass(Integer teacherId, Integer classId, Integer studentId) {
        User teacher = getVerifiedTeacher(teacherId);
        ClassEntity cls = getTeacherClass(teacher, classId);

        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        student.getClasses().remove(cls);
        userRepository.save(student);
    }

    // ── Assignment Management ─────────────────────────────────

    @Transactional
    public Map<String, Object> createAssignment(Integer teacherId, Integer classId,
                                                 String title, String description, String dueDate) {
        User teacher = getVerifiedTeacher(teacherId);
        ClassEntity cls = getTeacherClass(teacher, classId);

        Assignment a = new Assignment();
        a.setTitle(title.trim());
        a.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        a.setDueDate(dueDate        != null && !dueDate.isBlank()       ? dueDate.trim()       : null);
        a.setCreatedAt(LocalDate.now().toString());
        a.setClassEntity(cls);
        assignmentRepository.save(a);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",          a.getId());
        result.put("title",       a.getTitle());
        result.put("description", a.getDescription() != null ? a.getDescription() : "");
        result.put("dueDate",     a.getDueDate()     != null ? a.getDueDate()     : "");
        result.put("createdAt",   a.getCreatedAt());
        return result;
    }

    @Transactional
    public void deleteAssignment(Integer teacherId, Integer classId, Integer assignmentId) {
        User teacher = getVerifiedTeacher(teacherId);
        getTeacherClass(teacher, classId);

        Assignment a = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        if (!a.getClassEntity().getId().equals(classId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment does not belong to this class");
        }
        assignmentRepository.delete(a);
    }

    // ── Helpers ───────────────────────────────────────────────

    private User getVerifiedTeacher(Integer teacherId) {
        User teacher = userRepository.findById(teacherId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        if (teacher.getProfileType() != ProfileType.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a teacher account");
        }
        return teacher;
    }

    private ClassEntity getTeacherClass(User teacher, Integer classId) {
        return teacher.getClasses().stream()
            .filter(c -> c.getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Class not assigned to this teacher"));
    }
}
