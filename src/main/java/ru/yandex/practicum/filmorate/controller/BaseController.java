package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController<T extends BaseEntity> {
    protected final Map<Integer, T> storage = new HashMap<>();
    protected Integer newId = 0;

    public T create(T data) {
        int newId = generateId();
        data.setId(newId);
        storage.put(newId, data);
        return data;
    }

    public T update(T data) {
        storage.put(data.getId(), data);
        return data;
    }

    public Collection<T> getAll() {
        return storage.values();
    }

    private Integer generateId() {
        return ++newId;
    }

    protected abstract void validate(T data);
}
