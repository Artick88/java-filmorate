package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_CREATE_FILM = "INSERT INTO \"film\"(\"name\", \"description\", \"release_date\", " +
            "\"duration\", \"MPA_id\") VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE \"film\" SET \"name\"= ?, \"description\"= ?, " +
            "\"release_date\"= ?, \"duration\"= ?, \"MPA_id\"= ? WHERE \"id\"= ?";
    private static final String SQL_GET_ALL = "select \"id\", \"name\", \"description\", \"release_date\", \"duration\"," +
            " \"MPA_id\", \"created_at\" from \"film\"";
    private static final String SQL_GET_BY_ID = "SELECT \"id\", \"name\", \"description\", \"release_date\", \"duration\"," +
            " \"MPA_id\", \"created_at\" FROM \"film\" where \"id\" = ?";

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpaId(resultSet.getInt("MPA_id"))
                .build();
    }

    @Override
    public Film create(Film data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_CREATE_FILM, new String[]{"id"});
            ps.setString(1, data.getName());
            ps.setString(2, data.getDescription());
            ps.setDate(3, Date.valueOf(data.getReleaseDate()));
            ps.setLong(4, data.getDuration());
            ps.setInt(5, data.getMpa().getId());
            return ps;
        }, keyHolder);

        data.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return getById(data.getId());
    }

    @Override
    public Film update(Film data) {
        jdbcTemplate.update(SQL_UPDATE_FILM,
                data.getName(),
                data.getDescription(),
                data.getReleaseDate(),
                data.getDuration(),
                data.getMpa().getId(),
                data.getId());

        return getById(data.getId());
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL, this::mapRowToFilm);
    }

    @Override
    public Film getById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, this::mapRowToFilm, id);
    }
}