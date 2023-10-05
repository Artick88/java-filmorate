package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.ValidateMinDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {

    private static final int MAX_LENGTH_NAME = 200;
    private static final String MIN_RELEASE_DATE = "1895-12-28";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Integer id;

    @NotBlank(message = "Наименование фильма не может быть пустым")
    private String name;

    @Length(max = MAX_LENGTH_NAME, message = "Длина описания превысило максимальное допустимое ограничение")
    private String description;

    @DateTimeFormat(pattern = DATE_FORMAT)
    @ValidateMinDate(value = MIN_RELEASE_DATE,message = "Некорректная дата релиза")
    private LocalDate releaseDate;

    @Positive(message = "Длина фильма должна быть положительной")
    private Long duration;
}
