package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorStorage extends AbstractStorage<Director> {
    Set<Director> getDirectorsByFilmId(Integer directorId);

    Map<Integer, Set<Director>> getDirectorsByFilm(List<Film> films);

    void addDirectorByFilm(Integer filmId, Set<Director> directors);

    void deleteDirectorsByFilm(Integer filmId);
}
