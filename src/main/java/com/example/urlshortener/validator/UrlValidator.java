package com.example.urlshortener.validator;

import com.example.urlshortener.validator.annotation.ValidUrl;
import lombok.experimental.var;
import lombok.val;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
   public void initialize(final ValidUrl constraint) { }

   public boolean isValid(final String url, final ConstraintValidatorContext context) {
      return isNotBlank(url) && isUrlValid(url);
   }

   private boolean isUrlValid(final String url) {
      boolean valid = true;
      try {
         new URL(url);
      } catch (MalformedURLException e) {
         valid = false;
      }
      return valid;
   }
}
