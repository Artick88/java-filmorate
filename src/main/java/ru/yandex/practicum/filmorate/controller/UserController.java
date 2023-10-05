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
    protected int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Create user {}", user);
        int newId = generateId();
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(newId);
        users.put(newId, user);
        log.debug("Create user successfully");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Update user {}", user);

        if (user.getId() != null && !users.containsKey(user.getId())) {
            throw new ValidationException("Не найден пользователь");
        }

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Updated user successfully");
        return user;
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
