package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NoSuchEnumException extends RuntimeException {
    private final String object;

    public NoSuchEnumException(String message, String object) {
        super(message);
        this.object = object;
    }
}
