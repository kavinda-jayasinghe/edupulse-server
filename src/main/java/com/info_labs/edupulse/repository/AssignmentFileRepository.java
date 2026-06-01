package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Integer> {
    List<AssignmentFile> findByAssignmentIdOrderByIdAsc(Integer assignmentId);
    void deleteByAssignmentId(Integer assignmentId);
}
