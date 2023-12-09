package ru.yandex.practicum.filmorate.storage.film.like;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmLikesStorage {
    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Set<Integer> getUserLikesFilm(Integer filmId);

    Map<Integer, Set<Integer>> getUserLikesFilm(List<Film> films);

    Set<Integer> getFilmUserLikes(Integer userId);

    Map<Integer, List<Integer>> getAll();
}
