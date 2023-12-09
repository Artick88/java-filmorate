package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public void delete(Integer id) {
        validatedDirector(id);
        directorStorage.delete(id);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        validatedDirector(director.getId());
        return directorStorage.update(director);
    }

    public Director getById(Integer id) {
        validatedDirector(id);
        return directorStorage.getById(id);
    }

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public void validatedDirector(Integer id) {
        try {
            directorStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Не найдена запись с идентификатором", id);
        }
    }

    public Set<Director> getDirectorsByFilmId(Integer directorId) {
        return directorStorage.getDirectorsByFilmId(directorId);
    }

    public void addDirectorByFilm(Integer filmId, Set<Director> directors) {
        directorStorage.addDirectorByFilm(filmId, directors);
    }

    public void deleteDirectorsByFilm(Integer filmId) {
        directorStorage.deleteDirectorsByFilm(filmId);
    }

    public Map<Integer, Set<Director>> getDirectorsByFilm(List<Film> films) {
        return directorStorage.getDirectorsByFilm(films);
    }
}
