package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_DELETE_GENRE_BY_FILM = "DELETE FROM \"film_genre\" WHERE \"film_id\" = ?";
    private static final String SQL_ADD_GENRE_BY_FILM = "INSERT INTO \"film_genre\" (\"film_id\", \"genre_id\") VALUES(?, ?)";
    private static final String SQL_GET_BY_ID = "SELECT \"id\", \"code\", \"name\", \"description\" FROM \"genre\" WHERE \"id\" = ?";
    private static final String SQL_GET_ALL = "SELECT \"id\", \"code\", \"name\", \"description\", \"created_at\" FROM \"genre\"";
    private static final String SQL_GET_GENRES_BY_FILM = "SELECT fg.\"film_id\", g.\"id\", g.\"code\", g.\"name\", g.\"description\" " +
            "FROM \"film_genre\" fg JOIN \"genre\" g ON g.\"id\" = fg.\"genre_id\" WHERE \"film_id\" in (?)";

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
        return jdbcTemplate.query(SQL_GET_ALL, this::mapRowToGenre);
    }

    @Override
    public Genre getById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_BY_ID, this::mapRowToGenre, id);
    }

    @Override
    public void delete(Integer id) {
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }

    @Override
    public void addGenresByFilm(Integer filmId, Set<Genre> genres) {
        jdbcTemplate.batchUpdate(SQL_ADD_GENRE_BY_FILM,
                genres,
                100,
                (PreparedStatement ps, Genre genre) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                });
    }

    @Override
    public void deleteGenresByFilm(Integer filmId) {
        jdbcTemplate.update(SQL_DELETE_GENRE_BY_FILM, filmId);
    }

    @Override
    public Set<Genre> getGenresByFilmId(Integer filmId) {
        return jdbcTemplate.query(SQL_GET_GENRES_BY_FILM, this::mapRowToGenre, filmId).stream()
                .sorted(Comparator.comparingInt(BaseEntity::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Map<Integer, Set<Genre>> getGenresByFilms(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = SQL_GET_GENRES_BY_FILM.replace("?", inSql);

        Map<Integer, Set<Genre>> result = films.stream().collect(Collectors.toMap(Film::getId, Film::getGenres));

        jdbcTemplate.query(sqlQuery, (ResultSet rs) -> {
            int filmId = rs.getInt("film_id");
            Genre genre = Genre.builder()
                    .id(rs.getInt("id"))
                    .code(rs.getString("code"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .build();
            result.get(filmId).add(genre);
        }, result.keySet().toArray());

        return result;
    }
}