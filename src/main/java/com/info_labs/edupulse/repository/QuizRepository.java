package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    List<Quiz> findByTeacherIdOrderByIdDesc(Integer teacherId);
    List<Quiz> findByClassEntityIdOrderByIdDesc(Integer classId);
}
