package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Set;

public interface GenreStorage extends AbstractStorage<Genre> {
    void addGenresByFilm(Integer filmId, Set<Genre> genres);

    void deleteGenresByFilm(Integer filmId);

    Set<Genre> getGenresByFilmId(Integer filmId);
}