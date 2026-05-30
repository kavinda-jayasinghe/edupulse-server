package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    List<Exam> findByClassEntityInOrderByDate(Collection<ClassEntity> classes);
}
