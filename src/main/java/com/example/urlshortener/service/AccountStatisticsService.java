package com.example.urlshortener.service;

import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.model.RegisteredUrl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages statistics of short URL usage.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AccountStatisticsService {
    private AccountToUrlHashesRepository accountToUrlHashesRepository;
    private UrlRepository urlRepository;

    /**
     * Retrieves statistics of short URL usage for given account identifier.
     *
     * @param accountId account identifier
     * @return map with pairs that contains: key - registered URL and value - number of usage (redirects)
     */
    public Map<String, Integer> retrieveStatistics(final String accountId) {
        log.info("user with account id:{}, selects all his URLs", accountId);
        return accountToUrlHashesRepository.selectUrlsById(accountId).stream()
                .map(urlHash -> urlRepository.findByHash(urlHash))
                .filter(Optional::isPresent)
                .collect(Collectors.toMap(
                        registeredUrlOptional -> registeredUrlOptional.map(RegisteredUrl::getSourceUrl).orElse("unknown"),
                        registeredUrlOptional -> registeredUrlOptional.map(RegisteredUrl::getUsageCounter).orElse(0)
                ));
    }
}

