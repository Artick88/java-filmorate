package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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
    private static final String SQL_GET_ORDER_LIMIT = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\"," +
            " f.\"duration\", f.\"MPA_id\", f.\"created_at\", m.\"name\" mpa_name, m.\"description\" mpa_description FROM \"film\" f " +
            "JOIN MPA m ON m.\"id\" = f.\"MPA_id\" " +
            "LEFT JOIN \"film_likes\" fl ON f.\"id\" = fl.\"film_id\" " +
            "GROUP BY f.\"id\" ORDER BY COUNT(fl.\"user_id\") DESC LIMIT ?";

    private static final String SQL_GET_COMMON = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\"," +
            " f.\"duration\", f.\"MPA_id\", f.\"created_at\", m.\"name\" mpa_name, m.\"description\" mpa_description FROM \"film\" f " +
            "JOIN MPA m ON m.\"id\" = f.\"MPA_id\" " +
            "JOIN \"film_likes\" l ON f.\"id\" = l.\"film_id\" " +
            "JOIN \"film_likes\" lf ON f.\"id\" = lf.\"film_id\" " +
            "WHERE l.\"user_id\" = ? and lf.\"user_id\" = ?";

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
                .genres(new HashSet<>())
                .likesUser(new HashSet<>())
                .directors(new HashSet<>())
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
    public List<Film> getFilmsByDirectorId(Integer directorId) {
        return jdbcTemplate.query(SQL_GET_FILM_BY_DIRECTOR, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getTopFilmIds(Integer limit) {
        return jdbcTemplate.query(SQL_GET_ORDER_LIMIT,
                this::mapRowToFilm,
                limit);
    }

    @Override
    public List<Film> getCommonFriendFilms(int userId, int friendId) {
        return jdbcTemplate.query(SQL_GET_COMMON, this::mapRowToFilm, userId, friendId);
    }
}