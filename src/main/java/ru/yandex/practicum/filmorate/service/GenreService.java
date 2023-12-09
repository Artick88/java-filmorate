package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    final GenreStorage genreStorage;

    public Collection<Genre> getAll() {
        log.info("Get all genres");
        return genreStorage.getAll();
    }

    public void addGenresByFilm(Integer filmId, Set<Genre> genres) {
        genreStorage.addGenresByFilm(filmId, genres);
    }

    public void deleteGenresByFilm(Integer filmId) {
        genreStorage.deleteGenresByFilm(filmId);
    }

    public Set<Genre> getGenresByFilmId(Integer filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

    public Map<Integer, Set<Genre>> getGenresByFilms(List<Film> films) {
        return genreStorage.getGenresByFilms(films);
    }

    public Genre getById(Integer id) {
        //TODO: не определился как правильно сделать, мои варианты либо обернуть в Optional, либо перехватывать искл.
        log.info("Get genre by id {}", id);
        try {
            return genreStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Не найдена запись с идентификатором", id);
        }
    }
}