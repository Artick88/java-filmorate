package ru.yandex.practicum.filmorate.util.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumReleaseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface ValidateMinDate {
    String message() default "Дата не соответствует ожидаданиям";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}