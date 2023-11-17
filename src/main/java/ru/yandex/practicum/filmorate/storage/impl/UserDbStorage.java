package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_ALL_USERS = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\" FROM \"user\"";
    private static final String SQL_CREATE_USER = "INSERT INTO \"user\" (\"email\", \"login\", \"name\", \"birthday\") VALUES(? , ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE \"user\" SET \"email\"= ?, \"login\"= ?, \"name\"= ?, " +
            "\"birthday\"= ? WHERE \"id\"= ?";
    private static final String SQL_GET_USER_BY_ID = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\"" +
            "FROM \"user\" WHERE \"id\" = ?";

    @Override
    public User create(User data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_USER, new String[]{"id"});
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
        jdbcTemplate.update(SQL_UPDATE_USER,
                data.getEmail(),
                data.getLogin(),
                data.getName(),
                data.getBirthday(),
                data.getId());

        return getById(data.getId());
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_USERS, this::mapRowToUser);
    }

    @Override
    public User getById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_USER_BY_ID, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet resultSet, int numRow) throws SQLException {
        return User.builder()
                .name(resultSet.getString("name"))
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}