package com.example.urlshortener.validator.annotation;


import com.example.urlshortener.validator.UrlHashValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlHashValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD,  ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrlHash {
    String message() default "Invalid URL hash";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
