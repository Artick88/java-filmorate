package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Integer id = 0;
    private final Map<Integer, User> storage = new HashMap<>();

    @Override
    public User create(User user) {
        int newId = generatedId();
        user.setId(newId);
        storage.put(newId, user);
        return user;
    }

    @Override
    public User update(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User getUserById(Integer id) {
        return storage.get(id);
    }

    @Override
    public void resetId() {
        id = 0;
    }

    private Integer generatedId() {
        return ++id;
    }
}