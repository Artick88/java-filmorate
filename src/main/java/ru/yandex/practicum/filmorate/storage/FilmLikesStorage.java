package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmLikesStorage {
    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Set<Integer> getTopFilmIds(Integer limit);

    Set<Integer> getUserLikesFilm(Integer filmId);

    Set<Integer> getFilmUserLikes(Integer userId);

    Map<Integer, List<Integer>> getAll();
}
