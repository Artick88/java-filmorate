package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Create film {}", film);
        return super.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Update film {}", film);
        validate(film);
        return super.update(film);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("getAll films");
        return super.getAll();
    }

    @Override
    protected void validate(Film film) {
        if (film.getId() != null && !super.storage.containsKey(film.getId())) {
            throw new NotFoundException("Не найден фильм");
        }
    }
}
