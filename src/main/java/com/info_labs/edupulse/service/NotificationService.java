package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.Notification;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.repository.NotificationRepository;
import com.info_labs.edupulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    public List<Map<String, Object>> getNotifications(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return notificationRepository.findByUserOrderByDateDescIdDesc(user)
            .stream()
            .map(n -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",      n.getId());
                m.put("user_id", n.getUser().getId());
                m.put("message", n.getMessage());
                m.put("is_read", n.getIsRead());
                m.put("date",    n.getDate());
                return m;
            })
            .toList();
    }

    public void markRead(Integer notifId) {
        Notification n = notificationRepository.findById(notifId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    public void markAllRead(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Notification> list = notificationRepository.findByUserOrderByDateDescIdDesc(user);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
    }
}
