package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {

    private Integer id;

    @Email(message = "Некорректный емэйл")
    private String email;

    @NotBlank(message = "Логин обязательный")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть будущей")
    private LocalDate birthday;
}
