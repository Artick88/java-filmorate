package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final Integer object;

    public NotFoundException(String message, Integer object) {
        super(message);
        this.object = object;
    }
}