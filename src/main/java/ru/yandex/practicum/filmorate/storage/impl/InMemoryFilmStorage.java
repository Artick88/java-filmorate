package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Integer id = 0;
    private final Map<Integer, Film> storage = new HashMap<>();

    @Override
    public Film create(Film film) {
        int newId = generatedId();
        film.setId(newId);
        storage.put(newId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        storage.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        return storage.get(id);
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        storage.get(id).getLikesUser().add(userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        storage.get(id).getLikesUser().remove(userId);
    }

    @Override
    public void resetId() {
        id = 0;
    }

    @Override
    public boolean existsFilmById(Integer id) {
        return storage.containsKey(id);
    }

    private Integer generatedId() {
        return ++id;
    }
}