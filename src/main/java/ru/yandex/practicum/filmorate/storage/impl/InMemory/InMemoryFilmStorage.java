package ru.yandex.practicum.filmorate.storage.impl.InMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Component
public class InMemoryFilmStorage extends InMemoryBaseStorage<Film> implements FilmStorage {

}