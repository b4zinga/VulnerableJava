package com.example.vulnerablejava.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.example.vulnerablejava.validator.ImageNameValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageName {
    String message() default "Name长度必须等于3";
    Class<?>[] groups() default{};
    Class<?extends Payload>[] payload() default{};
}
