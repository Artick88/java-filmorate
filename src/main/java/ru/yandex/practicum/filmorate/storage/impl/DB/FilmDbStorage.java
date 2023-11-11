package ru.yandex.practicum.filmorate.storage.impl.DB;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDBStorage mpaDBStorage;
    private final GenreDBStorage genreDBStorage;

    @Override
    public Film create(Film data) {
        String sqlQuery = "INSERT INTO \"film\"(\"name\", \"description\", \"release_date\", \"duration\", \"MPA_id\")" +
                "VALUES(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, data.getName());
            stmt.setString(2, data.getDescription());
            stmt.setDate(3, Date.valueOf(data.getReleaseDate()));
            stmt.setLong(4, data.getDuration());
            stmt.setInt(5, data.getMpa().getId());
            return stmt;
        }, keyHolder);

        data.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        addGenresByFilm(data.getId(), data.getGenres());

        return getById(data.getId());
    }

    @Override
    public Film update(Film data) {
        String sqlQuery = "UPDATE \"film\"" +
                "SET \"name\"= ?, \"description\"= ?, \"release_date\"= ?, \"duration\"= ?, \"MPA_id\"= ?" +
                "WHERE \"id\"= ?";

        jdbcTemplate.update(sqlQuery,
                data.getName(),
                data.getDescription(),
                data.getReleaseDate(),
                data.getDuration(),
                data.getMpa().getId(),
                data.getId());

        //Обновим жанры
        updateGenresFilmById(data);

        //Обновим лайки
        updateLikesUserFilmById(data);

        return getById(data.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select \"id\", \"name\", \"description\", \"release_date\", \"duration\", \"MPA_id\", \"created_at\"" +
                " from \"film\"";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getById(Integer id) {
        String sqlQuery = "SELECT \"id\", \"name\", \"description\", \"release_date\", \"duration\", \"MPA_id\", \"created_at\"" +
                "FROM \"film\" where \"id\" = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        Integer filmId = resultSet.getInt("id");
        Integer mpaId = resultSet.getInt("MPA_id");

        MPA mpa = mpaDBStorage.getById(mpaId);
        Set<Genre> genres = getGenresByFilmId(filmId);
        Set<Integer> likesUserId = getUserIdLikesFilmById(filmId);

        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(mpa)
                .genres(genres)
                .likesUser(likesUserId)
                .build();
    }

    private Set<Genre> getGenresByFilmId(Integer filmId) {
        String sqlQuery = "SELECT \"genre_id\" FROM \"film_genre\" WHERE  \"film_id\" = ? ORDER BY \"genre_id\"";

        List<Integer> genresId = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("genre_id"), filmId);

        return genresId.stream()
                .map(genreDBStorage::getById)
                .sorted(Comparator.comparingInt(BaseEntity::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Integer> getUserIdLikesFilmById(Integer filmId) {
        String sqlQuery = "SELECT \"id\", \"user_id\", \"created_at\" FROM \"film_likes\" WHERE \"film_id\" = ?";

        return Set.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    private void updateGenresFilmById(Film film) {
        //так как жанры сейчас отдельно к фильму не добавляют,
        //то просто удаляем все существующие и добавляем новые
        deleteGenresFilmById(film.getId());
        addGenresByFilm(film.getId(), film.getGenres());
    }

    private void addGenresByFilm(Integer filmId, Set<Genre> genres) {
        String sqlQuery = "INSERT INTO \"film_genre\" (\"film_id\", \"genre_id\") VALUES(?, ?)";

        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }
    }

    private void deleteGenresFilmById(Integer filmId) {
        String sqlQuery = "DELETE FROM \"film_genre\" WHERE \"film_id\"= ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void updateLikesUserFilmById(Film film) {
        //Получим лайки из БД - получим лайки из запроса
        Set<Integer> currentLikesUser = getUserIdLikesFilmById(film.getId());
        Set<Integer> newLikesUser = film.getLikesUser();

        //удалим лайки, которые уже есть в БД - добавим новые в БД
        Set<Integer> addLikesUser = new HashSet<>(newLikesUser);
        addLikesUser.removeAll(currentLikesUser);
        addLikesUserFilm(film.getId(), addLikesUser);

        //сделаем обратное, чтобы удалить лишние лайки из БД
        Set<Integer> removeLikesUser = new HashSet<>(currentLikesUser);
        removeLikesUser.removeAll(newLikesUser);
        deleteLikesUserFilm(film.getId(), removeLikesUser);
    }

    private void addLikesUserFilm(Integer filmId, Set<Integer> likesUser) {
        String sqlQuery = "INSERT INTO \"film_likes\" (\"film_id\", \"user_id\") VALUES(?, ?)";

        for (Integer userId : likesUser) {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        }
    }

    private void deleteLikesUserFilm(Integer filmId, Set<Integer> likesUser) {
        String sqlQuery = "DELETE FROM \"film_likes\" WHERE \"film_id\"= ? AND \"user_id\"= ?";

        for (Integer userId : likesUser) {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        }
    }
}