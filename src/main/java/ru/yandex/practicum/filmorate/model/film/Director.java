package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.BaseEntity;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Director extends BaseEntity {
    @NotBlank
    private String name;
}