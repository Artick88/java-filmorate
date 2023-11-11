package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.BaseEntity;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Status extends BaseEntity {
    private String code;
    private String name;
    private String description;
}