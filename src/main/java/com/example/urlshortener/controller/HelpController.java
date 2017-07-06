package com.example.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;

/**
 * Redirects requests to the main page {@code /} and {@code /help} endpoint to static html page with service description.
 */
@RestController
public class HelpController {

    @GetMapping("/help")
    public ResponseEntity help() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header(LOCATION, "/help/help.html")
                .build();
    }

    @GetMapping("/")
    public ResponseEntity index() {
        return ResponseEntity
                .status(PERMANENT_REDIRECT)
                .header(LOCATION, "/help/help.html")
                .build();
    }
}
