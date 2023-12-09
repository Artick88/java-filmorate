package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatusDBStorage implements StatusStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_ALL = "SELECT \"id\", \"code\", \"name\", \"description\" FROM \"status_type\"";
    private static final String SQL_GET_BY_ID = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" " +
            "FROM \"status_type\" WHERE \"id\" = ?";
    private static final String SQL_GET_BY_CODE = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" " +
            "FROM \"status_type\" WHERE UPPER(\"code\") = ?";

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
        return jdbcTemplate.query(SQL_GET_ALL, this::mapRowToStatus);
    }

    @Override
    public Status getById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, this::mapRowToStatus, id);
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public Status getByCode(String code) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_CODE, this::mapRowToStatus, code.toUpperCase());
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