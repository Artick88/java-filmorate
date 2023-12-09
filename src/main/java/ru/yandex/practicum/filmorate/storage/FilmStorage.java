package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Set;


public interface FilmStorage extends AbstractStorage<Film> {
    Set<Film> getFilmsByDirectorId(Integer directorId);
}