package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.UserFriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserFriendsDBStorage implements UserFriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<UserFriends> getFriendsByUserId(Integer userId) {
        String sqlQuery = "SELECT \"id\", \"user_from_id\", \"user_to_id\", \"status_id\"" +
                "FROM \"user_friend\" WHERE \"user_from_id\" = ?";

        return jdbcTemplate.queryForStream(sqlQuery, this::mapRowToUserFriends, userId)
                .collect(Collectors.toSet());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId, Integer statusId) {
        String sqlQuery = "INSERT INTO \"user_friend\" (\"user_from_id\", \"user_to_id\", \"status_id\") VALUES(?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, statusId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM \"user_friend\" WHERE \"user_from_id\"= ? AND \"user_to_id\" = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    private UserFriends mapRowToUserFriends(ResultSet resultSet, int numRow) throws SQLException {
        return UserFriends.builder()
                .id(resultSet.getInt("id"))
                .userId(resultSet.getInt("user_to_id"))
                .statusId(resultSet.getInt("status_id"))
                .build();
    }
}