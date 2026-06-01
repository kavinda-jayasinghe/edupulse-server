package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.McqAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface McqAnswerRepository extends JpaRepository<McqAnswer, Integer> {
    List<McqAnswer> findByQuizIdOrderByQuestionNumberAsc(Integer quizId);
    void deleteByQuizId(Integer quizId);
}
