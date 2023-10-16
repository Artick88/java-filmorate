package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        validateFindUserById(user.getId());
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
        validateFindUserById(id);
        validateFindUserById(friendId);
        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        log.info("Delete user {} friend {}", id, friendId);
        validateFindUserById(id);
        validateFindUserById(friendId);
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
    }

    public List<User> getFriends(Integer id) {
        log.debug("Get friends, user {}", id);
        validateFindUserById(id);
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.debug("User {} get mutual friend {}", id, otherId);
        validateFindUserById(id);
        validateFindUserById(otherId);
        Set<Integer> friends = userStorage.getUserById(id).getFriends();
        Set<Integer> otherFriends = userStorage.getUserById(otherId).getFriends();
        if (friends == null || otherFriends == null) {
            return new ArrayList<>();
        }
        return friends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(Integer id) {
        log.debug("Get user by id {}", id);
        validateFindUserById(id);
        return userStorage.getUserById(id);
    }

    public void resetId() {
        log.debug("Reset id users");
        userStorage.resetId();
    }

    public void validateFindUserById(Integer id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
        }
    }
}