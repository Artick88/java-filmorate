package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikesStorage filmLikesStorage;
    private final GenreStorage genreStorage;
    private final MpaService mpaService;

    @Transactional
    public Film create(Film film) {
        log.info("Create film {}", film);
        Film createdFilm = filmStorage.create(film);
        updateGenres(film);
        createdFilm.setMpa(mpaService.getById((createdFilm.getMpa().getId())));
        createdFilm.setGenres(genreStorage.getGenresByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    @Transactional
    public Film update(Film film) {
        log.info("Update film {}", film);
        validateFindFilmById(film.getId());
        Film savedFilm = filmStorage.update(film);
        updateGenres(film);
        savedFilm.setMpa(mpaService.getById(savedFilm.getMpa().getId()));
        savedFilm.setGenres(genreStorage.getGenresByFilmId(savedFilm.getId()));
        return savedFilm;
    }

    public List<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll().stream()
                .peek(film -> film.setMpa(mpaService.getById(film.getMpa().getId())))
                .peek(film -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addLike(Integer id, Integer userId) {
        log.info("Add like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);

        filmLikesStorage.addLike(id, userId);
    }

    @Transactional
    public void deleteLike(Integer id, Integer userId) {
        log.info("Delete like film {} user {}", id, userId);
        userService.validateFindUserById(userId);
        validateFindFilmById(id);

        filmLikesStorage.deleteLike(id, userId);
    }

    public List<Film> getTopLikeFilms(Integer count) {
        log.debug("Get top like films, count {}", count);

        return filmLikesStorage.getTopFilmIds(count)
                .stream()
                .map(filmStorage::getById)
                .peek(film -> film.setMpa(mpaService.getById(film.getMpa().getId())))
                .peek(film -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .sorted((v1, v2) -> v2.getLikesUser().size() - v1.getLikesUser().size())
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        log.debug("Get film by id {}", id);
        validateFindFilmById(id);
        Film film = filmStorage.getById(id);
        film.setMpa(mpaService.getById(film.getMpa().getId()));
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId()));
        return film;
    }

    private void updateGenres(Film film) {
        //Так как жанры отдельно не добавляют, значит зачищаем все имеющие и заново добавляем
        genreStorage.deleteGenresByFilm(film.getId());
        genreStorage.addGenresByFilm(film.getId(), film.getGenres());
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