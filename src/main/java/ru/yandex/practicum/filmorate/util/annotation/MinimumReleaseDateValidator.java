package ru.yandex.practicum.filmorate.util.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinimumReleaseDateValidator implements ConstraintValidator<ValidateMinDate, LocalDate> {
    private LocalDate value;

    @Override
    public void initialize(ValidateMinDate constraintAnnotation) {
        value = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return value.isBefore(localDate);
    }
}
