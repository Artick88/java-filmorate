package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;

import java.sql.ResultSet;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmLikesDBStorage implements FilmLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_USERS_LIKE_FILM = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\" " +
            "WHERE \"film_id\" = ?";
    private static final String SQL_GET_FILMS_USER_LIKES = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\" " +
            "WHERE \"user_id\" = ?";
    private static final String SQL_GET_ALL_FILMS_LIKES = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\"";
    private static final String SQL_ADD_LIKE = "INSERT INTO \"film_likes\" (\"film_id\", \"user_id\") VALUES(?, ?)";
    private static final String SQL_DELETE_LIKE = "DELETE FROM \"film_likes\" WHERE \"film_id\"= ? AND \"user_id\"= ?";
    private static final String SQL_GET_ORDER_LIMIT = "SELECT f.\"id\" FROM \"film\" f " +
            "LEFT JOIN \"film_likes\" fl ON f.\"id\" = fl.\"film_id\" " +
            "GROUP BY f.\"id\" ORDER BY COUNT(fl.\"user_id\") DESC LIMIT ?";

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    @Override
    public Set<Integer> getTopFilmIds(Integer limit) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_ORDER_LIMIT,
                (rs, rowNum) -> rs.getInt("id"),
                limit));
    }

    @Override
    public Set<Integer> getUserLikesFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_USERS_LIKE_FILM,
                (rs, rowNum) -> rs.getInt("user_id"), filmId));
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
