package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    protected int id;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Create film {}", film);
        int newId = generateId();
        film.setId(newId);
        films.put(newId, film);
        log.debug("Create film successfully");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Update film {}", film);

        if (film.getId() != null && !films.containsKey(film.getId())) {
            throw new ValidationException("Не найден фильм");
        }

        films.put(film.getId(), film);
        log.debug("Update film successfully");
        return film;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("getAll films");
        return films.values();
    }

    private int generateId() {
        return ++id;
    }
}
