package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    @Transactional
    public User create(User user) {
        log.info("Create user {}", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    @Transactional
    public User update(User user) {
        log.info("Update user {}", user);
        validateFindUserById(user.getId());
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    @Transactional
    public List<User> getAll() {
        log.debug("Get all users");
        return userStorage.getAll();
    }

    @Transactional
    public void addFriend(Integer id, Integer friendId) {
        log.info("Add user {} friend {}", id, friendId);
        validateFindUserById(id);
        validateFindUserById(friendId);

        User user = userStorage.getById(id);
        user.getFriends().add(UserFriends.builder()
                .userId(friendId)
                .build());

        userStorage.update(user);
    }

    @Transactional
    public void deleteFriend(Integer id, Integer friendId) {
        log.info("Delete user {} friend {}", id, friendId);
        validateFindUserById(id);
        validateFindUserById(friendId);

        User user = userStorage.getById(id);

        user.setFriends(user.getFriends().stream()
                .filter(friend -> !friend.getUserId().equals(friendId))
                .collect(Collectors.toSet()));

        userStorage.update(user);
    }

    @Transactional
    public List<User> getFriends(Integer id) {
        log.debug("Get friends, user {}", id);
        validateFindUserById(id);
        return userStorage.getById(id).getFriends().stream()
                .map(UserFriends::getUserId)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.debug("User {} get mutual friend {}", id, otherId);
        validateFindUserById(id);
        validateFindUserById(otherId);
        Set<Integer> friends = userStorage.getById(id).getFriends().stream()
                .map(UserFriends::getUserId)
                .collect(Collectors.toSet());
        Set<Integer> otherFriends = userStorage.getById(otherId).getFriends().stream()
                .map(UserFriends::getUserId)
                .collect(Collectors.toSet());

        if (friends.isEmpty() || otherFriends.isEmpty()) {
            return new ArrayList<>();
        }
        return friends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    @Transactional
    public User getUserById(Integer id) {
        log.debug("Get user by id {}", id);
        validateFindUserById(id);
        return userStorage.getById(id);
    }

    public void validateFindUserById(Integer id) {
        //TODO: не определился как правильно сделать, мои варианты либо обернуть в Optional, либо перехватывать искл.
        try {
            if (userStorage.getById(id) == null) {
                throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
        }
    }
}