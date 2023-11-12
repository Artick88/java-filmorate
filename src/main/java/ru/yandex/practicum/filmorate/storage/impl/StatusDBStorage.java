package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.Status;
import ru.yandex.practicum.filmorate.storage.StatusStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatusDBStorage implements StatusStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Status create(Status data) {
        return null;
    }

    @Override
    public Status update(Status data) {
        return null;
    }

    @Override
    public List<Status> getAll() {
        String sqlQuery = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" " +
                "FROM \"status_type\"";

        return jdbcTemplate.query(sqlQuery, this::mapRowToStatus);
    }

    @Override
    public Status getById(Integer id) {
        String sqlQuery = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" " +
                "FROM \"status_type\" WHERE \"id\" = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToStatus, id);
    }

    public Status findIdByCode(String code) {
        String sqlQuery = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" " +
                "FROM \"status_type\" WHERE UPPER(\"code\") = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToStatus, code.toUpperCase());
    }

    private Status mapRowToStatus(ResultSet resultSet, int numRow) throws SQLException {
        return Status.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}