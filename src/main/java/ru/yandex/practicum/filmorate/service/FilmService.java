package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(Integer id, Integer userId) {
        userStorage.validateFindUserById(userId);
        filmStorage.validateFindFilmById(id);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        userStorage.validateFindUserById(userId);
        filmStorage.validateFindFilmById(id);
        filmStorage.deleteLike(id, userId);

    }

    public List<Film> getTopLikeFilms(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((v1, v2) -> v2.getLikesUser().size() - v1.getLikesUser().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void resetId(){
        filmStorage.resetId();
    }
}