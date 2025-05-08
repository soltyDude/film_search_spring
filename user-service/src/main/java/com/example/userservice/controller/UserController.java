package com.example.userservice.controller;

import com.example.userservice.service.UserService;
import com.example.userservice.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");

        User user = userService.registerUser(username, email, password);
        return ResponseEntity.ok(Map.of("message", "User registered successfully", "userId", user.getId()));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        logger.info("Проверка наличия пользователя с ID={}", id);
        boolean exists = userService.userExistsById(id);
        logger.info("Результат проверки: {}", exists);
        return ResponseEntity.ok(exists);
    }
}
