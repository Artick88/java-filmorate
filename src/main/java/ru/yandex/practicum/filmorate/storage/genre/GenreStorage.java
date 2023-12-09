package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage extends AbstractStorage<Genre> {
    void addGenresByFilm(Integer filmId, Set<Genre> genres);

    void deleteGenresByFilm(Integer filmId);

    Set<Genre> getGenresByFilmId(Integer filmId);

    Map<Integer, Set<Genre>> getGenresByFilms(List<Film> films);
}