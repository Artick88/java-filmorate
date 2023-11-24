package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Hidden
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
}