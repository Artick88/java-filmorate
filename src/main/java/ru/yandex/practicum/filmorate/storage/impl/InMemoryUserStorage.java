package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

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
        validateFindUserById(user.getId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User getUserById(Integer id) {
        validateFindUserById(id);
        return storage.get(id);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        validateFindUserById(id);
        validateFindUserById(friendId);
        storage.get(id).getFriends().add(friendId);
        storage.get(friendId).getFriends().add(id);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        validateFindUserById(id);
        validateFindUserById(friendId);
        storage.get(id).getFriends().remove(friendId);
        storage.get(friendId).getFriends().remove(id);
    }

    @Override
    public List<User> getFriends(Integer id) {
        validateFindUserById(id);
        return storage.get(id).getFriends().stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        validateFindUserById(id);
        validateFindUserById(otherId);
        Set<Integer> friends = storage.get(id).getFriends();
        Set<Integer> otherFriends = storage.get(otherId).getFriends();
        if (friends == null || otherFriends == null) {
            return new ArrayList<>();
        }
        return friends.stream()
                .filter(otherFriends::contains)
                .map(storage::get)
                .collect(Collectors.toList());
    }

    @Override
    public void validateFindUserById(Integer id) {
        if (storage.get(id) == null) {
            throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
        }
    }

    @Override
    public void resetId() {
        id = 0;
    }

    private Integer generatedId() {
        return ++id;
    }
}