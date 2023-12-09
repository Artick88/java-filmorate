package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.like.FilmLikesStorage;
import ru.yandex.practicum.filmorate.util.enumeration.SortType;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikesStorage filmLikesStorage;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Transactional
    public Film create(Film film) {
        log.info("Create film {}", film);
        Film createdFilm = filmStorage.create(film);
        updateGenres(film);
        updateDirectors(film);
        createdFilm.setGenres(genreService.getGenresByFilmId(createdFilm.getId()));
        createdFilm.setDirectors(directorService.getDirectorsByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    @Transactional
    public Film update(Film film) {
        log.info("Update film {}", film);
        validateFindFilmById(film.getId());
        Film savedFilm = filmStorage.update(film);
        updateGenres(film);
        updateDirectors(film);
        savedFilm.setGenres(genreService.getGenresByFilmId(savedFilm.getId()));
        savedFilm.setDirectors(directorService.getDirectorsByFilmId(savedFilm.getId()));
        return savedFilm;
    }

    public List<Film> getAll() {
        log.debug("Get all films");

        List<Film> films = filmStorage.getAll();

        Map<Integer, Set<Genre>> filmGenresMap = genreService.getGenresByFilms(films);
        Map<Integer, Set<Integer>> filmLikesMap = filmLikesStorage.getUserLikesFilm(films);
        Map<Integer, Set<Director>> filmDirectorsMap = directorService.getDirectorsByFilm(films);

        return filmStorage.getAll().stream()
                .peek(film -> {
                    film.setGenres(filmGenresMap.get(film.getId()).stream()
                            .sorted(Comparator.comparingInt(BaseEntity::getId))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));
                    film.setLikesUser(filmLikesMap.get(film.getId()));
                    film.setDirectors(filmDirectorsMap.get(film.getId()));
                }).collect(Collectors.toList());
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

        List<Film> films = filmStorage.getTopFilmIds(count);

        Map<Integer, Set<Genre>> filmGenresMap = genreService.getGenresByFilms(films);
        Map<Integer, Set<Integer>> filmLikesMap = filmLikesStorage.getUserLikesFilm(films);
        Map<Integer, Set<Director>> filmDirectorsMap = directorService.getDirectorsByFilm(films);

        return films.stream()
                .peek(film -> {
                    film.setGenres(filmGenresMap.get(film.getId()));
                    film.setLikesUser(filmLikesMap.get(film.getId()));
                    film.setDirectors(filmDirectorsMap.get(film.getId()));
                }).sorted((v1, v2) -> v2.getLikesUser().size() - v1.getLikesUser().size())
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        log.debug("Get film by id {}", id);
        validateFindFilmById(id);
        Film film = filmStorage.getById(id);
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId()));
        film.setDirectors(directorService.getDirectorsByFilmId(film.getId()));
        return film;
    }

    public List<Film> getCommonFilms(int userId, int friendsId) {
        userService.validateFindUserById(userId);
        userService.validateFindUserById(friendsId);

        List<Film> films = filmStorage.getCommonFriendFilms(userId, friendsId);

        if (films.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Set<Genre>> filmGenresMap = genreService.getGenresByFilms(films);
        Map<Integer, Set<Integer>> filmLikesMap = filmLikesStorage.getUserLikesFilm(films);
        Map<Integer, Set<Director>> filmDirectorsMap = directorService.getDirectorsByFilm(films);

        return films.stream()
                .peek(film -> {
                    film.setGenres(filmGenresMap.get(film.getId()));
                    film.setLikesUser(filmLikesMap.get(film.getId()));
                    film.setDirectors(filmDirectorsMap.get(film.getId()));
                }).collect(Collectors.toList());
    }

    public List<Film> getFilmsDirector(Integer directorId, SortType sortBy) {
        directorService.validatedDirector(directorId);

        List<Film> films = filmStorage.getFilmsByDirectorId(directorId);

        Map<Integer, Set<Genre>> filmGenresMap = genreService.getGenresByFilms(films);
        Map<Integer, Set<Integer>> filmLikesMap = filmLikesStorage.getUserLikesFilm(films);
        Map<Integer, Set<Director>> filmDirectorsMap = directorService.getDirectorsByFilm(films);

        return films.stream()
                .peek(film -> {
                            film.setDirectors(filmDirectorsMap.get(film.getId()));
                            film.setLikesUser(filmLikesMap.get(film.getId()));
                            film.setGenres(filmGenresMap.get(film.getId()));
                        }
                ).sorted((o1, o2) -> {
                    if (sortBy.equals(SortType.LIKES)) {
                        if (!o1.getLikesUser().isEmpty() && !o2.getLikesUser().isEmpty()) {
                            return o1.getLikesUser().size() - o2.getLikesUser().size();
                        }
                        return o1.getId() - o2.getId();
                    } else if (sortBy.equals(SortType.YEAR)) {
                        return o1.getReleaseDate().getYear() - o2.getReleaseDate().getYear();
                    } else {
                        return o1.getId() - o2.getId();
                    }
                }).collect(Collectors.toList());
    }

    private void updateGenres(Film film) {
        //Так как жанры отдельно не добавляют, значит зачищаем все имеющие и заново добавляем
        genreService.deleteGenresByFilm(film.getId());
        genreService.addGenresByFilm(film.getId(), film.getGenres());
    }

    private void updateDirectors(Film film) {
        //Так как директоры отдельно не добавляют, значит зачищаем все имеющие и заново добавляем
        directorService.deleteDirectorsByFilm(film.getId());
        directorService.addDirectorByFilm(film.getId(), film.getDirectors());
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