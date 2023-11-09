package com.example.vulnerablejava.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import com.example.vulnerablejava.annotation.ImageName;

public class ImageNameValidator implements ConstraintValidator<ImageName, String> {

    private String message;

    @Override
    public void initialize(ImageName imageNameAnnotation) {
        this.message = imageNameAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value.length() == 3 ? true : false;
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            String errorMsg = this.message + ", invalid name: " + value;

            // 修复漏洞，使用参数化消息模板 代替 字符串串联
            HibernateConstraintValidatorContext safeContext = context.unwrap(HibernateConstraintValidatorContext.class);
            safeContext.addExpressionVariable("userPovidedValue", errorMsg);
            context.buildConstraintViolationWithTemplate("${userPovidedValue}").addConstraintViolation();
        }
        return isValid;
    }
}
