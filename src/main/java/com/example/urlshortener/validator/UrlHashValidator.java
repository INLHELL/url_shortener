package com.example.urlshortener.validator;

import com.example.urlshortener.validator.annotation.ValidUrlHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.example.urlshortener.main.GlobalAppConstants.HASH_LENGTH;

@Slf4j
public class UrlHashValidator implements ConstraintValidator<ValidUrlHash, String> {
   public void initialize(final ValidUrlHash constraint) { }

   public boolean isValid(final String urlHash, final ConstraintValidatorContext context) {
      log.info("passed URL hash: {} will be validated", urlHash);
      return StringUtils.isAlphanumeric(urlHash) && urlHash.length() == HASH_LENGTH;
   }
}
