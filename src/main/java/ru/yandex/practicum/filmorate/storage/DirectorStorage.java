package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.Set;

public interface DirectorStorage extends AbstractStorage<Director> {
    Set<Director> getDirectorsByFilmId(Integer directorId);

    void addDirectorByFilm(Integer filmId, Set<Director> directors);

    void deleteDirectorsByFilm(Integer filmId);
}
