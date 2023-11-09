package com.example.vulnerablejava.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.example.vulnerablejava.validator.ImageURLValidator;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageURLValidator.class)
public @interface ImageURL {
    String message() default "URL必须为https";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
