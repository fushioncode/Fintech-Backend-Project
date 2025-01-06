package com.fintech.bepc.custom.validators;

import com.fintech.bepc.custom.annotations.ValidPhoneNumber;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

//    @Override
//    public void initialize(ValidPhoneNumber constraintAnnotation) {
//
//    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(value, "NG");
            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (Exception e) {
            return false;
        }
    }
}

