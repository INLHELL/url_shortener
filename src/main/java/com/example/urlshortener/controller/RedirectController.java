package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlRedirectService;
import com.example.urlshortener.validator.annotation.ValidUrlHash;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * Describes endpoint for redirecting clients that come with shorten URL to target source URL that was registered by {@link UrlRedirectService} controller.
 */
@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class RedirectController {

    private UrlRedirectService urlRedirectService;

    /**
     * Redirects client to target source URL if this URL was registered before and URL hash was successfully generated for it.
     *
     * @param urlHash url hash that was generated during URL registration
     * @return response with 3xx redirect status code that was passed during registration and {@code Location} header with target source URL
     */
    @GetMapping(value = "/{urlHash}")
    public ResponseEntity redirectToUrl(@PathVariable @ValidUrlHash final String urlHash) {
        log.info("/{} redirect request processed", urlHash);
        val optionalRegisteredUrl = urlRedirectService.findRegisteredUrl(urlHash);
        log.info("/{} redirect request to: {}, was successfully processed: {}",
                urlHash,
                optionalRegisteredUrl.isPresent() ? optionalRegisteredUrl.get().getSourceUrl() : "-",
                optionalRegisteredUrl.isPresent());
        return optionalRegisteredUrl
                .map(registeredUrl -> ResponseEntity
                        .status(registeredUrl.getRedirectType())
                        .header(LOCATION, registeredUrl.getSourceUrl())
                        .build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
