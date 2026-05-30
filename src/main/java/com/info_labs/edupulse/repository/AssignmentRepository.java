package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByClassEntityIdOrderByIdDesc(Integer classId);
}
