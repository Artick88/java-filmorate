package ru.yandex.practicum.filmorate.storage.impl.DB;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre data) {
        return null;
    }

    @Override
    public Genre update(Genre data) {
        return null;
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" FROM \"genre\"";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getById(Integer id) {
        String sqlQuery = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" FROM \"genre\" WHERE \"id\" = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}