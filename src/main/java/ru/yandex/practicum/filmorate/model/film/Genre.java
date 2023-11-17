package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.BaseEntity;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Genre extends BaseEntity {
    private String code;
    private String name;
    private String description;
}