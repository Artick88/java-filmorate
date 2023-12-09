package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_CREATE_DIRECTOR = "INSERT INTO \"director\"(\"name\") VALUES(?)";
    private static final String SQL_GET_BY_ID = "SELECT \"id\", \"name\" FROM \"director\" WHERE \"id\" = ?";
    private static final String SQL_GET_ALL = "SELECT \"id\", \"name\" FROM \"director\"";
    private static final String SQL_UPDATE_DIRECTOR = "UPDATE \"director\" SET \"name\"= ? WHERE \"id\"= ?";
    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM \"director\" WHERE \"id\" = ?";
    private static final String SQL_GET_DIRECTOR_BY_FILM = "SELECT d.\"id\", d.\"name\" FROM \"director\" d " +
            "JOIN \"film_director\" fd ON d.\"id\" = fd.\"director_id\" WHERE fd.\"film_id\" = ?";
    private static final String SQL_DELETE_DIRECTOR_BY_FILM = "DELETE FROM \"film_director\" WHERE \"film_id\" = ?";
    private static final String SQL_ADD_RELATION_DIRECTOR_AND_FILM = "INSERT INTO \"film_director\" (\"film_id\", \"director_id\") VALUES(?, ?)";

    @Override
    public Director create(Director data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_CREATE_DIRECTOR, new String[]{"id"});
            ps.setString(1, data.getName());
            return ps;
        }, keyHolder);

        data.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return data;
    }

    @Override
    public Director update(Director data) {
        jdbcTemplate.update(SQL_UPDATE_DIRECTOR,
                data.getName(),
                data.getId());

        return getById(data.getId());
    }

    @Override
    public List<Director> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL, this::mapRowTo);
    }

    @Override
    public Director getById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, this::mapRowTo, id);
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }

    private Director mapRowTo(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Set<Director> getDirectorsByFilmId(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_DIRECTOR_BY_FILM, this::mapRowTo, filmId));
    }

    @Override
    public void addDirectorByFilm(Integer filmId, Set<Director> directors) {
        jdbcTemplate.batchUpdate(SQL_ADD_RELATION_DIRECTOR_AND_FILM,
                directors,
                100,
                (PreparedStatement ps, Director director) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, director.getId());
                });
    }

    @Override
    public void deleteDirectorsByFilm(Integer filmId) {
        jdbcTemplate.update(SQL_DELETE_DIRECTOR_BY_FILM, filmId);
    }
}
