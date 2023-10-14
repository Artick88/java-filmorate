package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Create film {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Update film {}", film);
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll();
    }

    public void addLike(Integer id, Integer userId) {
        log.info("Add like film {} user {}", id, userId);
        userStorage.validateFindUserById(userId);
        filmStorage.validateFindFilmById(id);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        log.info("Delete like film {} user {}", id, userId);
        userStorage.validateFindUserById(userId);
        filmStorage.validateFindFilmById(id);
        filmStorage.deleteLike(id, userId);

    }

    public List<Film> getTopLikeFilms(Integer count) {
        log.debug("Get top like films, count {}", count);
        return filmStorage.getAll().stream()
                .sorted((v1, v2) -> v2.getLikesUser().size() - v1.getLikesUser().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        log.debug("Get film by id {}", id);
        return filmStorage.getFilmById(id);
    }

    public void resetId(){
        log.debug("Reset film storage id");
        filmStorage.resetId();
    }
}