package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/{id}")
    public Director getById(@PathVariable Integer id) {
        return directorService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Integer id) {
        directorService.delete(id);
    }

    @GetMapping()
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }
}
