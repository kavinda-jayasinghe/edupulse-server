package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.utils.ProfileType;
import com.info_labs.edupulse.repository.ClassRepository;
import com.info_labs.edupulse.repository.StudentExamRepository;
import com.info_labs.edupulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ClassRepository       classRepository;
    private final UserRepository        userRepository;
    private final StudentExamRepository studentExamRepository;

    public List<Map<String, Object>> getRankings(Integer classId) {
        ClassEntity cls = classRepository.findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        // Aggregated scores for students who have submitted at least one exam in this class
        List<Object[]> rows = studentExamRepository.rankingDataByClass(cls);
        Map<Integer, Map<String, Object>> byId = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer id  = ((Number) row[0]).intValue();
            String name = (String) row[1];
            long exams  = ((Number) row[2]).longValue();
            long avg    = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            long total  = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id",         id);
            entry.put("name",       name);
            entry.put("exams",      exams);
            entry.put("avgScore",   avg);
            entry.put("totalScore", total);
            byId.put(id, entry);
        }

        // Include enrolled students with 0 exam submissions
        userRepository.findByClassesContaining(cls).stream()
            .filter(u -> u.getProfileType() == ProfileType.STUDENT && !byId.containsKey(u.getId()))
            .forEach(u -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id",         u.getId());
                entry.put("name",       u.getName());
                entry.put("exams",      0L);
                entry.put("avgScore",   0L);
                entry.put("totalScore", 0L);
                byId.put(u.getId(), entry);
            });

        // Sort by totalScore desc and assign rank
        List<Map<String, Object>> sorted = new ArrayList<>(byId.values());
        sorted.sort((a, b) -> Long.compare((Long) b.get("totalScore"), (Long) a.get("totalScore")));
        for (int i = 0; i < sorted.size(); i++) sorted.get(i).put("rank", i + 1);
        return sorted;
    }
}
