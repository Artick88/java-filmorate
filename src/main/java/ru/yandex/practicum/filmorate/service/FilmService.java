package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.util.enumeration.SortType;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikesStorage filmLikesStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final DirectorService directorService;

    @Transactional
    public Film create(Film film) {
        log.info("Create film {}", film);
        Film createdFilm = filmStorage.create(film);
        updateGenres(film);
        updateDirectors(film);
        createdFilm.setGenres(genreStorage.getGenresByFilmId(createdFilm.getId()));
        createdFilm.setDirectors(directorStorage.getDirectorsByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    @Transactional
    public Film update(Film film) {
        log.info("Update film {}", film);
        validateFindFilmById(film.getId());
        Film savedFilm = filmStorage.update(film);
        updateGenres(film);
        updateDirectors(film);
        savedFilm.setGenres(genreStorage.getGenresByFilmId(savedFilm.getId()));
        savedFilm.setDirectors(directorStorage.getDirectorsByFilmId(savedFilm.getId()));
        return savedFilm;
    }

    public List<Film> getAll() {
        log.debug("Get all films");
        return filmStorage.getAll().stream()
                .peek(film -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId())))
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
                .peek(film -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .peek(film -> film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId())))
                .sorted((v1, v2) -> v2.getLikesUser().size() - v1.getLikesUser().size())
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        log.debug("Get film by id {}", id);
        validateFindFilmById(id);
        Film film = filmStorage.getById(id);
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId()));
        film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        return film;
    }

    public List<Film> getCommonFilms(int userId, int friendsId) {
        userService.validateFindUserById(userId);
        userService.validateFindUserById(friendsId);

        Set<Integer> filmIds = filmLikesStorage.getFilmUserLikes(userId);
        Set<Integer> friendFilmIds = filmLikesStorage.getFilmUserLikes(friendsId);

        if (filmIds.isEmpty() || friendFilmIds.isEmpty()) {
            return new ArrayList<>();
        }

        return filmIds.stream()
                .filter(friendFilmIds::contains)
                .map(filmStorage::getById)
                .peek(film -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .collect(Collectors.toList());
    }

    public List<Film> getFilmsDirector(Integer directorId, SortType sortBy) {
        directorService.validatedDirector(directorId);
        List<Film> films = filmStorage.getFilmsByDirectorId(directorId)
                .stream()
                .peek(film -> {
                            film.setDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
                            film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId()));
                            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
                        }
                )
                .collect(Collectors.toList());

        if (sortBy.equals(SortType.LIKES)) {
            return films.stream()
                    .sorted((o1, o2) -> {
                        if (!o1.getLikesUser().isEmpty() && !o2.getLikesUser().isEmpty()) {
                            return o1.getLikesUser().size() - o2.getLikesUser().size();
                        }
                        return o1.getId() - o2.getId();
                    })
                    .collect(Collectors.toList());
        } else if (sortBy.equals(SortType.YEAR)) {
            return films.stream()
                    .sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                    .collect(Collectors.toList());
        }
        return films;
    }

    private void updateGenres(Film film) {
        //Так как жанры отдельно не добавляют, значит зачищаем все имеющие и заново добавляем
        genreStorage.deleteGenresByFilm(film.getId());
        genreStorage.addGenresByFilm(film.getId(), film.getGenres());
    }

    private void updateDirectors(Film film) {
        //Так как директоры отдельно не добавляют, значит зачищаем все имеющие и заново добавляем
        directorStorage.deleteDirectorsByFilm(film.getId());
        directorStorage.addDirectorByFilm(film.getId(), film.getDirectors());
    }

    public void validateFindFilmById(Integer id) {
        try {
            if (filmStorage.getById(id) == null) {
                throw new NotFoundException(String.format("Не найден фильм %d", id), id);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Не найден фильм %d", id), id);
        }
    }
}