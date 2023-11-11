package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    final MpaService mpaService;

    @GetMapping
    public Collection<MPA> getAll() {
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable Integer id) {
        return mpaService.getById(id);
    }
}