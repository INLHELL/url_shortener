package com.example.urlshortener.validator.annotation;

import com.example.urlshortener.validator.RedirectCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RedirectCodeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRedirectCode {
    String message() default "Invalid redirect code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
