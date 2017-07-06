package com.example.urlshortener.validator;

import com.example.urlshortener.validator.annotation.ValidRedirectCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

public class RedirectCodeValidator implements ConstraintValidator<ValidRedirectCode, Integer> {
   public void initialize(final ValidRedirectCode constraint) {}

   public boolean isValid(final Integer redirectCode, final ConstraintValidatorContext context) {
      return redirectCode == FOUND.value() || redirectCode == MOVED_PERMANENTLY.value();
   }
}
