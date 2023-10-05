package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int id = 0;
    private Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Create user {}", user);
        try {
            int newId = generateId();
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(newId);
            users.put(newId, user);
            log.debug("Create user successfully");
            return user;
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new ValidationException("Не валидный запрос");
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Update user {}", user);
        try {
            if (user.getId() != null && !users.containsKey(user.getId())) {
                throw new ValidationException("Не найден пользователь");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.debug("Updated user successfully");
            return user;
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new ValidationException("Не валидный запрос");
        }
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("getAll users");
        return users.values();
    }

    private int generateId() {
        return ++id;
    }
}
