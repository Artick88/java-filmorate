package ru.yandex.practicum.filmorate.storage.user.friend;

import ru.yandex.practicum.filmorate.model.user.UserFriends;

import java.util.Set;

public interface UserFriendsStorage {

    Set<UserFriends> getFriendsByUserId(Integer userId);

    void addFriend(Integer userId, Integer friendId, Integer statusId);

    void deleteFriend(Integer userId, Integer friendId);
}