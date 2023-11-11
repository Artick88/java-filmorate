package ru.yandex.practicum.filmorate.storage.impl.InMemory;

import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.*;

public class InMemoryBaseStorage<T extends BaseEntity> implements AbstractStorage<T> {

    private Integer id = 0;
    private final Map<Integer, T> storage = new HashMap<>();

    @Override
    public T create(T data) {
        int newId = generatedId();
        data.setId(newId);
        storage.put(newId, data);
        return data;
    }

    @Override
    public T update(T data) {
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public T getById(Integer id) {
        return storage.get(id);
    }

    public void resetId() {
        id = 0;
    }

    private Integer generatedId() {
        return ++id;
    }
}
