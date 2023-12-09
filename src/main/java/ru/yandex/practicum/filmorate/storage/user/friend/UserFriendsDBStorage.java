package ru.yandex.practicum.filmorate.storage.user.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.Status;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.util.enumeration.StatusFriends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserFriendsDBStorage implements UserFriendsStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_FRIENDS = "SELECT \"id\", \"user_from_id\", \"user_to_id\", \"status_id\"" +
            "FROM \"user_friend\" WHERE \"user_from_id\" = ?";
    private static final String SQL_ADD_FRIEND = "INSERT INTO \"user_friend\" (\"user_from_id\", \"user_to_id\", \"status_id\") " +
            "VALUES(?, ?, (SELECT \"id\" FROM \"status_type\" WHERE UPPER(\"code\") = ?))";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM \"user_friend\" WHERE \"user_from_id\"= ? AND \"user_to_id\" = ?";


    @Override
    public Set<UserFriends> getFriendsByUserId(Integer userId) {
        return jdbcTemplate.queryForStream(SQL_GET_FRIENDS, this::mapRowToUserFriends, userId)
                .collect(Collectors.toSet());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId, StatusFriends status) {
        jdbcTemplate.update(SQL_ADD_FRIEND, userId, friendId, status.toString().toUpperCase());
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(SQL_DELETE_FRIEND, userId, friendId);
    }

    private UserFriends mapRowToUserFriends(ResultSet resultSet, int numRow) throws SQLException {
        return UserFriends.builder()
                .id(resultSet.getInt("id"))
                .user(User.builder().id(resultSet.getInt("user_to_id")).build())
                .status(Status.builder().id(resultSet.getInt("status_id")).build())
                .build();
    }
}