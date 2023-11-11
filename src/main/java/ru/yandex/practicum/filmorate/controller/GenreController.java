package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) {
        return genreService.getById(id);
    }
}