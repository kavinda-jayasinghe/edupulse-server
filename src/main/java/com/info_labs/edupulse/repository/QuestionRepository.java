package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.Exam;
import com.info_labs.edupulse.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByExamOrderByNumber(Exam exam);
}
