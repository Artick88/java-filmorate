package ru.yandex.practicum.filmorate.storage.user.friend;

import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.util.enumeration.StatusFriends;

import java.util.Set;

public interface UserFriendsStorage {

    Set<UserFriends> getFriendsByUserId(Integer userId);

    void addFriend(Integer userId, Integer friendId, StatusFriends status);

    void deleteFriend(Integer userId, Integer friendId);
}