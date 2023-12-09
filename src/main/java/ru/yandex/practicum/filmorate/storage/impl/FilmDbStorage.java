package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_CREATE_FILM = "INSERT INTO \"film\"(\"name\", \"description\", \"release_date\", " +
            "\"duration\", \"MPA_id\") VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE \"film\" SET \"name\"= ?, \"description\"= ?, " +
            "\"release_date\"= ?, \"duration\"= ?, \"MPA_id\"= ? WHERE \"id\"= ?";
    private static final String SQL_GET_ALL = "select f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\"," +
            " f.\"MPA_id\", f.\"created_at\", m.\"name\" mpa_name, m.\"description\" mpa_description from \"film\" f " +
            "JOIN MPA m ON m.\"id\" = f.\"MPA_id\"";
    private static final String SQL_GET_BY_ID = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", " +
            "f.\"MPA_id\", f.\"created_at\", m.\"name\" mpa_name, m.\"description\" mpa_description FROM \"film\" f " +
            "JOIN MPA m ON m.\"id\" = f.\"MPA_id\" " +
            "WHERE f.\"id\" = ?";

    private static final String SQL_GET_FILM_BY_DIRECTOR = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\"," +
            " f.\"duration\", f.\"MPA_id\", f.\"created_at\", m.\"name\" mpa_name, m.\"description\" mpa_description  FROM \"director\" d " +
            "JOIN \"film_director\" fd ON d.\"id\" = fd.\"director_id\" " +
            "JOIN \"film\" f ON f.\"id\" = fd.\"film_id\" " +
            "JOIN MPA m ON m.\"id\" = f.\"MPA_id\" " +
            "WHERE fd.\"director_id\" = ?";

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(MPA.builder()
                        .id(resultSet.getInt("MPA_id"))
                        .name(resultSet.getString("mpa_name"))
                        .description(resultSet.getString("mpa_description"))
                        .build())
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

    @Override
    public void delete(Integer id) {
    }

    @Override
    public Set<Film> getFilmsByDirectorId(Integer directorId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_FILM_BY_DIRECTOR, this::mapRowToFilm, directorId));
    }
}