package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {

    @Email(message = "Некорректный емэйл")
    private String email;

    @NotBlank(message = "Логин обязательный")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть будущей")
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();
}
