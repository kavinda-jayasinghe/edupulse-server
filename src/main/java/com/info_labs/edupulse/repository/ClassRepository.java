package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    boolean existsByClassCode(String classCode);
}
