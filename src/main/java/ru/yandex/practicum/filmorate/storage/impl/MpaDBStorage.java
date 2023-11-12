package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDBStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA create(MPA data) {
        return null;
    }

    @Override
    public MPA update(MPA data) {
        return null;
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "SELECT \"id\", \"name\", \"description\" FROM MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public MPA getById(Integer id) {
        String sqlQuery = "SELECT \"id\", \"name\", \"description\" FROM MPA WHERE \"id\" = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    private MPA mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}