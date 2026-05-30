package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.utils.ProfileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    List<User> findByClassesContaining(ClassEntity classEntity);
    List<User> findByProfileType(ProfileType profileType);
    long countByProfileType(ProfileType profileType);
    Page<User> findByProfileTypeNot(ProfileType profileType, Pageable pageable);
}
