package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getAll();

    User getUserById(Integer id);

    void addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getMutualFriends(Integer id, Integer otherId);

    void validateFindUserById(Integer id);

    void resetId();
}