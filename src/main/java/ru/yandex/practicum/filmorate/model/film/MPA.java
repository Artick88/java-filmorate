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
public class MPA extends BaseEntity {
    private String name;
    private String description;
}