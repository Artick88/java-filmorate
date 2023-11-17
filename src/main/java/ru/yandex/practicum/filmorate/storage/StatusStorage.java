package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.user.Status;

public interface StatusStorage extends AbstractStorage<Status> {
    Status getByCode(String code);
}