package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.Status;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.StatusFriends.NOT_APPROVED;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final StatusDBStorage statusDBStorage;

    @Override
    public User create(User data) {
        String sqlQuery = "INSERT INTO \"user\" (\"email\", \"login\", \"name\", \"birthday\") VALUES(? , ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, data.getEmail());
            stmt.setString(2, data.getLogin());
            stmt.setString(3, data.getName());
            stmt.setDate(4, Date.valueOf(data.getBirthday()));
            return stmt;
        }, keyHolder);

        data.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return data;
    }

    @Override
    public User update(User data) {
        String sqlQuery = "UPDATE \"user\"" +
                "SET \"email\"= ?, \"login\"= ?, \"name\"= ?, \"birthday\"= ?" +
                "WHERE \"id\"= ?";

        jdbcTemplate.update(sqlQuery,
                data.getEmail(),
                data.getLogin(),
                data.getName(),
                data.getBirthday(),
                data.getId());

        updateFriendsUser(data);

        return getById(data.getId());
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\", \"created_at\" FROM \"user\"";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getById(Integer id) {
        String sqlQuery = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\", \"created_at\" " +
                "FROM \"user\" WHERE \"id\" = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet resultSet, int numRow) throws SQLException {
        Integer userId = resultSet.getInt("id");
        Set<UserFriends> userFriends = getFriendsUserById(userId);

        return User.builder()
                .name(resultSet.getString("name"))
                .id(userId)
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(userFriends)
                .build();
    }

    private Set<UserFriends> getFriendsUserById(Integer userId) {
        String sqlQuery = "SELECT \"id\", \"user_from_id\", \"user_to_id\", \"status_id\"" +
                "FROM \"user_friend\" WHERE \"user_from_id\" = ?";

        return jdbcTemplate.queryForStream(sqlQuery, this::mapRowToUserFriends, userId)
                .collect(Collectors.toSet());
    }

    private UserFriends mapRowToUserFriends(ResultSet resultSet, int numRow) throws SQLException {
        Status status = statusDBStorage.getById(resultSet.getInt("status_id"));

        return UserFriends.builder()
                .id(resultSet.getInt("id"))
                .userId(resultSet.getInt("user_to_id"))
                .status(status)
                .build();
    }

    private void updateFriendsUser(User user) {
        //Получим друзей из БД - получим друзей из запроса
        Set<Integer> currentFriendsId = getFriendsUserById(user.getId()).stream()
                .map(UserFriends::getUserId)
                .collect(Collectors.toSet());

        Set<Integer> newFriendsId = user.getFriends().stream()
                .map(UserFriends::getUserId)
                .collect(Collectors.toSet());

        //удалим друзей, которые уже есть в БД - добавим новые в БД
        Set<Integer> addFriends = new HashSet<>(newFriendsId);
        addFriends.removeAll(currentFriendsId);
        addFriendsUser(user.getId(), addFriends);

        //сделаем обратное, чтобы удалить лишних друзей из БД
        Set<Integer> removeFriends = new HashSet<>(currentFriendsId);
        removeFriends.removeAll(newFriendsId);
        deleteFriendsUser(user.getId(), removeFriends);
    }

    private void addFriendsUser(Integer userId, Set<Integer> friendsId) {
        String sqlQuery = "INSERT INTO \"user_friend\" (\"user_from_id\", \"user_to_id\", \"status_id\") VALUES(?, ?, ?)";

        //По умолчанию создаем в статусе "Не подтверждено"
        Integer statusNotApprovedId = statusDBStorage.findIdByCode(NOT_APPROVED.toString()).getId();

        for (Integer friendId : friendsId) {
            jdbcTemplate.update(sqlQuery, userId, friendId, statusNotApprovedId);
        }
    }

    private void deleteFriendsUser(Integer userId, Set<Integer> friendsId) {
        String sqlQuery = "DELETE FROM \"user_friend\" WHERE \"user_from_id\"= ? AND \"user_to_id\" = ?";
        for (Integer friendId : friendsId) {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        }
    }
}