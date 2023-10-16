package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film create(Film film) {
        log.info("Create film {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Update film {}", film);
        validateFindFilmById(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll();
    }

    public void addLike(Integer id, Integer userId) {
        log.info("Add like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);
        filmStorage.getFilmById(id).getLikesUser().add(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        log.info("Delete like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);
        filmStorage.getFilmById(id).getLikesUser().remove(userId);
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
        validateFindFilmById(id);
        return filmStorage.getFilmById(id);
    }

    public void resetId() {
        log.debug("Reset film storage id");
        filmStorage.resetId();
    }

    public void validateFindFilmById(Integer id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException(String.format("Не найден фильм %d", id), id);
        }
    }
}