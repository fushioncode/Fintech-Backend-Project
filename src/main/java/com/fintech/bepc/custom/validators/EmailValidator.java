package com.fintech.bepc.custom.validators;


import com.fintech.bepc.custom.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

//    @Override
//    public void initialize(ValidEmail constraintAnnotation) {
//    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Pattern.matches(EMAIL_REGEX, value);
    }
}

