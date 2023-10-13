package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController<User> {

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Create user {}", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return super.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Update user {}", user);
        validate(user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return super.update(user);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("getAll users");
        return super.getAll();
    }

    protected void validate(User user) {
        if (user.getId() != null && !super.storage.containsKey(user.getId())) {
            throw new NotFoundException("Не найден фильм");
        }
    }
}
