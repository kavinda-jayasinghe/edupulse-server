package com.info_labs.edupulse.service;

import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.Notification;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.utils.ProfileType;
import com.info_labs.edupulse.repository.ClassRepository;
import com.info_labs.edupulse.repository.NotificationRepository;
import com.info_labs.edupulse.repository.UserRepository;
import com.info_labs.edupulse.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository         userRepository;
    private final ClassRepository        classRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;

    public List<ClassEntity> getClasses() {
        return classRepository.findAll();
    }

    public Map<String, Object> login(String mobile, String password) {
        User user = userRepository.findByMobile(mobile)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mobile number or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mobile number or password");
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been disabled. Please contact an administrator.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getMobile());

        // Build list of enrolled classes (sorted by id for consistency)
        List<Map<String, Object>> classesList = user.getClasses().stream()
            .sorted(Comparator.comparing(ClassEntity::getId))
            .map(c -> Map.<String, Object>of("id", c.getId(), "name", c.getName()))
            .collect(Collectors.toList());

        // Primary class = first enrolled class (for frontend backwards-compat)
        Integer primaryClassId   = classesList.isEmpty() ? null : (Integer) classesList.get(0).get("id");
        String  primaryClassName = classesList.isEmpty() ? ""   : (String)  classesList.get(0).get("name");

        return Map.of(
            "token", token,
            "user", Map.of(
                "id",          user.getId(),
                "name",        user.getName(),
                "mobile",      user.getMobile(),
                "profileType", user.getProfileType() != null ? user.getProfileType().name() : ProfileType.STUDENT.name(),
                "class_id",    primaryClassId != null ? primaryClassId : 0,
                "class_name", primaryClassName,
                "classes",    classesList
            )
        );
    }

    public void register(String name, String mobile, String password) {
        if (!mobile.matches("^[0-9]{10}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number must be exactly 10 digits");
        }
        if (userRepository.existsByMobile(mobile)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
        }

        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        user.setPassword(passwordEncoder.encode(password));
        user.setProfileType(ProfileType.STUDENT);
        userRepository.save(user);

        Notification notif = new Notification();
        notif.setUser(user);
        notif.setMessage("Welcome to EduPulse, " + name + "! Start exploring your exams.");
        notif.setIsRead(false);
        notif.setDate(LocalDate.now().toString());
        notificationRepository.save(notif);
    }
}
