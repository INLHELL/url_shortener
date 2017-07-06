package com.example.urlshortener.controller;

import com.example.urlshortener.service.AccountStatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Provides statistics about number of redirects for each registered by passed user URLs.
 */
@RestController
@Slf4j
@AllArgsConstructor
public class StatisticsController {

    private AccountStatisticsService accountStatisticsService;

    /**
     * Endpoint returns list that contains key-value pairs, key - registered URL, value - number of usage.
     * <br/>
     * URL usage means, how many times user access generated shorten URL.
     * <br />
     * Shorten URL consist of further elements: scheme + :// + host + port + / url_hash
     * <br />
     * Passed {@code accountId} as path variable must be the same as {@code accountId} that passed with {@code Authorization} header
     *
     * @param accountId passed account identifier passed as path variable
     * @param principal inject by Spring represents user that was extracted from {@code Authorization} header
     * @return list that contains number of redirects for each registered URL
     */
    @GetMapping(
            path = "/statistics/{accountId}",
            produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity statistics(@PathVariable final String accountId, final Principal principal) {
        log.info("/statistics/{} request processed", accountId);
        log.info("/statistics/{} user with account id:{} has access to view statistics for account:{}", accountId, principal.getName(), accountId);
        return ResponseEntity.ok(accountStatisticsService.retrieveStatistics(accountId));
    }
}
