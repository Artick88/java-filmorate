package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Transactional
    public Film create(Film film) {
        log.info("Create film {}", film);
        return filmStorage.create(film);
    }

    @Transactional
    public Film update(Film film) {
        log.info("Update film {}", film);
        validateFindFilmById(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll();
    }

    @Transactional
    public void addLike(Integer id, Integer userId) {
        log.info("Add like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);

        Film film = filmStorage.getById(id);
        Set<Integer> likesUser = new HashSet<>(film.getLikesUser());
        likesUser.add(userId);
        film.setLikesUser(likesUser);

        filmStorage.update(film);
    }

    @Transactional
    public void deleteLike(Integer id, Integer userId) {
        log.info("Delete like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);

        Film film = filmStorage.getById(id);
        Set<Integer> likesUser = new HashSet<>(film.getLikesUser());
        likesUser.remove(userId);
        film.setLikesUser(likesUser);

        filmStorage.update(film);
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
        return filmStorage.getById(id);
    }

    public void validateFindFilmById(Integer id) {
        //TODO: не определился как правильно сделать, мои варианты либо обернуть в Optional, либо перехватывать искл.
        try {
            if (filmStorage.getById(id) == null) {
                throw new NotFoundException(String.format("Не найден фильм %d", id), id);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Не найден фильм %d", id), id);
        }
    }
}