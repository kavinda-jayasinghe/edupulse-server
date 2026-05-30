package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.Exam;
import com.info_labs.edupulse.entity.StudentExam;
import com.info_labs.edupulse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentExamRepository extends JpaRepository<StudentExam, Integer> {
    List<StudentExam> findByStudent(User student);
    Optional<StudentExam> findByStudentAndExam(User student, Exam exam);
    boolean existsByStudentAndExam(User student, Exam exam);
    void deleteByStudent(User student);

    @Query("SELECT COUNT(se) FROM StudentExam se WHERE se.exam = :exam AND se.score > :score")
    long countByExamAndScoreGreaterThan(Exam exam, int score);

    // Rankings: aggregate scores for all students enrolled in the given class
    @Query("SELECT se.student.id, se.student.name, COUNT(se), ROUND(AVG(se.score)), SUM(se.score) " +
           "FROM StudentExam se WHERE :cls MEMBER OF se.student.classes " +
           "GROUP BY se.student.id, se.student.name")
    List<Object[]> rankingDataByClass(@Param("cls") ClassEntity cls);

    // Stats: total submissions and avg score for a class
    @Query("SELECT COUNT(se), AVG(se.score) FROM StudentExam se WHERE :cls MEMBER OF se.student.classes")
    List<Object[]> classExamStats(@Param("cls") ClassEntity cls);
}
