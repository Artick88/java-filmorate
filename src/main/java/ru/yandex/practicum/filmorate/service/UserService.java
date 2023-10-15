package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        log.info("Create user {}", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        log.info("Update user {}", user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getAll() {
        log.debug("Get all users");
        return userStorage.getAll();
    }

    public void addFriend(Integer id, Integer friendId) {
        log.info("Add user {} friend {}", id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        log.info("Delete user {} friend {}", id, friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(Integer id) {
        log.debug("Get friends, user {}", id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.debug("User {} get mutual friend {}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    public User getUserById(Integer id) {
        log.debug("Get user by id {}", id);
        return userStorage.getUserById(id);
    }

    public void resetId() {
        log.debug("Reset id users");
        userStorage.resetId();
    }
}