package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmLikesDBStorage implements FilmLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_USERS_LIKE_FILM = "SELECT \"id\", \"film_id\", \"user_id\" FROM \"film_likes\" " +
            "WHERE \"film_id\" = ?";
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
}
