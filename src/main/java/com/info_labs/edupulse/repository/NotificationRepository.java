package com.info_labs.edupulse.repository;

import com.info_labs.edupulse.entity.Notification;
import com.info_labs.edupulse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserOrderByDateDescIdDesc(User user);
    void deleteByUser(User user);
}
