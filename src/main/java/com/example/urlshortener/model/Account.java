package com.example.urlshortener.model;

import lombok.Value;

import static com.example.urlshortener.main.GlobalAppConstants.PASSWORD_LENGTH;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Value
public class Account {
    private final String id;
    private final String password = randomAlphanumeric(PASSWORD_LENGTH);
}
