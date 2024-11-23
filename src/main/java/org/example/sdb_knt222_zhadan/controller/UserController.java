package org.example.sdb_knt222_zhadan.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.sdb_knt222_zhadan.config.JwtTokenProvider;
import org.example.sdb_knt222_zhadan.model.User;
import org.example.sdb_knt222_zhadan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Отримання всіх користувачів");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()) != null) {
            return "Користувач із таким email вже існує.";
        }
        userService.addUser(user);
        return "Реєстрація успішна!";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null || !user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний email або пароль");
        }
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        if (token != null) {
            jwtTokenProvider.blacklistToken(token);
            return ResponseEntity.ok("Ви успішно вийшли з системи");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Токен не знайдено");
    }


    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Ваша роль: " + authentication.getAuthorities().toString());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неавторизований");
        }
    }


    @GetMapping("/check")
    public ResponseEntity<String> checkToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Токен дійсний. Ви увійшли як " + authentication.getName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен недійсний або відсутній");
        }
    }
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getUserByEmail(authentication.getName());
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<String> updateProfile(@RequestBody User updatedUser, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser = userService.getUserByEmail(authentication.getName());
            currentUser.setFirstname(updatedUser.getFirstname());
            currentUser.setLastname(updatedUser.getLastname());
            if (!updatedUser.getPassword().isEmpty()) {
                currentUser.setPassword(updatedUser.getPassword());
            }
            currentUser.setPhone(updatedUser.getPhone());

            userService.updateUser(currentUser);
            return ResponseEntity.ok("Профіль успішно оновлено");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не авторизовано");
        }
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<String> updateUserByAdmin(@PathVariable int userId, @RequestBody User updatedUser) {
        logger.info("Оновлення користувача з ID: {}", userId);

        User user = userService.getUserById(userId);
        if (user != null) {
            user.setFirstname(updatedUser.getFirstname());
            user.setLastname(updatedUser.getLastname());
            if (!updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            user.setPhone(updatedUser.getPhone());
            user.setRole(updatedUser.getRole());

            userService.updateUser(user);
            return ResponseEntity.ok("Користувача успішно оновлено");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        logger.info("Видалення користувача з ID: {}", userId);

        User user = userService.getUserById(userId);
        if (user != null) {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Користувача успішно видалено");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }
    }
}