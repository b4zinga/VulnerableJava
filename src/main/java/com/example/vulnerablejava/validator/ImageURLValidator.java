package com.example.vulnerablejava.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.vulnerablejava.annotation.ImageURL;

public class ImageURLValidator implements ConstraintValidator<ImageURL, String> {

    private String message;

    @Override
    public void initialize(ImageURL imageURLAnnotation) {
        this.message = imageURLAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value.startsWith("https://");
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            String errorMsg = this.message + ", invalid url: " + value;
            context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
        }
        return isValid;
    }
}
