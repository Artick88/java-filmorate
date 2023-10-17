package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface AbstractStorage<T extends BaseEntity> {

    T create(T data);

    T update(T data);

    List<T> getAll();

    T getById(Integer id);

    void resetId();
}
