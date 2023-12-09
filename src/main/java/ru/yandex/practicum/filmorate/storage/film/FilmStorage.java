package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;


public interface FilmStorage extends AbstractStorage<Film> {
    List<Film> getFilmsByDirectorId(Integer directorId);

    List<Film> getTopFilmIds(Integer limit);

    List<Film> getCommonFriendFilms(int userId, int friendId);
}