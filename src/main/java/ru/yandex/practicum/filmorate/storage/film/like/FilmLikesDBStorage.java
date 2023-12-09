package ru.yandex.practicum.filmorate.storage.film.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmLikesDBStorage implements FilmLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_USERS_LIKE_FILM = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\" " +
            "WHERE \"film_id\" IN (?)";
    private static final String SQL_GET_FILMS_USER_LIKES = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\" " +
            "WHERE \"user_id\" = ?";
    private static final String SQL_GET_ALL_FILMS_LIKES = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\"";
    private static final String SQL_ADD_LIKE = "INSERT INTO \"film_likes\" (\"film_id\", \"user_id\") VALUES(?, ?)";
    private static final String SQL_DELETE_LIKE = "DELETE FROM \"film_likes\" WHERE \"film_id\"= ? AND \"user_id\"= ?";

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    @Override
    public Set<Integer> getUserLikesFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_USERS_LIKE_FILM,
                (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    @Override
    public Map<Integer, Set<Integer>> getUserLikesFilm(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = SQL_GET_USERS_LIKE_FILM.replace("?", inSql);

        Map<Integer, Set<Integer>> result = films.stream().collect(Collectors.toMap(Film::getId, Film::getLikesUser));

        jdbcTemplate.query(sqlQuery, (ResultSet rs) -> {
            int filmId = rs.getInt("film_id");
            int userId = rs.getInt("user_id");
            result.get(filmId).add(userId);
        }, result.keySet().toArray());

        return result;
    }

    @Override
    public Set<Integer> getFilmUserLikes(Integer userId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_FILMS_USER_LIKES,
                (rs, rowNum) -> rs.getInt("film_id"), userId));
    }

    @Override
    public Map<Integer, List<Integer>> getAll() {
        HashMap<Integer, List<Integer>> result = new HashMap<>();
        jdbcTemplate.query(SQL_GET_ALL_FILMS_LIKES, (ResultSet rs) -> {
            Integer userId = rs.getInt("user_id");
            Integer filmId = rs.getInt("film_id");
            if (!result.containsKey(userId)) {
                result.put(userId, new ArrayList<>());
            }
            result.get(userId).add(filmId);
        });
        return result;
    }
}
