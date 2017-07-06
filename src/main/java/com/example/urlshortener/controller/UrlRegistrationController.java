package com.example.urlshortener.controller;


import com.example.urlshortener.service.UrlRegistrationService;
import com.example.urlshortener.validator.annotation.ValidRedirectCode;
import com.example.urlshortener.validator.annotation.ValidUrl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Endpoint provides possibility for registered users to shorten URLs.
 */
@RestController
@Slf4j
@AllArgsConstructor
public class UrlRegistrationController {

    private final UrlRegistrationService urlRegistrationService;
    private final Environment env;

    /**
     * Registers (stores) passed URL with optional redirect status code (301 or 302) and returns its shorten version in response.
     *
     * @param registeringUrlRequest contains redirect status code and URL that will be shorten
     * @param principal injected by Spring, account identifier will be extracted from it to associate particular user with passed URL
     * @return shorten version of passed URL
     */
    @PostMapping(
            path = "/register",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public RegisteredShortUrlResponse registerUrl(
            @RequestBody @Valid final RegisteringUrlRequest registeringUrlRequest,
            final Principal principal) throws UnknownHostException {
        log.info("/register request processed, for user with account id:{}", principal.getName());
        val urlHash = urlRegistrationService.register(
                principal.getName(),
                registeringUrlRequest.url,
                registeringUrlRequest.redirectType);
        log.info("/register request processed successfully, for user with account id:{}, url hash:{} was stored", principal.getName(), urlHash);
        return new RegisteredShortUrlResponse(urlHash, env.getProperty("local.server.port"));
    }


    @Data
    private static final class RegisteringUrlRequest {
        @JsonProperty("url")
        @ValidUrl
        private final String url;

        @JsonProperty("redirectType")
        @ValidRedirectCode
        private final int redirectType = FOUND.value();
    }

    @Value
    private static final class RegisteredShortUrlResponse {

        private final static String SCHEME = "http";

        @JsonProperty("shortUrl")
        private final String urlHash;

        RegisteredShortUrlResponse(final String urlHash, final String port) throws UnknownHostException {
            String hostId = System.getenv("HEROKU_APP_ID") +"|"+System.getenv("HEROKU_APP_NAME")+"|"+System.getenv("HEROKU_DYNO_ID");
            this.urlHash = SCHEME + "://" + hostId +  (isNull(port) || port.equals("80") ? EMPTY : ":" + port) + "/" + urlHash;
        }
    }

}
