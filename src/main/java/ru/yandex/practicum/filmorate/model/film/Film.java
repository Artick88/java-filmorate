package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.util.annotation.ValidateMinDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Film extends BaseEntity {

    private static final int MAX_LENGTH_NAME = 200;
    private static final String MIN_RELEASE_DATE = "1895-12-28";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @NotBlank(message = "Наименование фильма не может быть пустым")
    private String name;

    @Length(max = MAX_LENGTH_NAME, message = "Длина описания превысило максимальное допустимое ограничение")
    private String description;

    @DateTimeFormat(pattern = DATE_FORMAT)
    @ValidateMinDate(value = MIN_RELEASE_DATE, message = "Некорректная дата релиза")
    private LocalDate releaseDate;

    @Positive(message = "Длина фильма должна быть положительной")
    private Long duration;

    private MPA mpa;

    private Set<Genre> genres = new HashSet<>();

    private Set<Integer> likesUser = new HashSet<>();

    private Set<Director> directors = new HashSet<>();
}
