package com.example.urlshortener.service;

import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.model.RegisteredUrl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import static com.example.urlshortener.main.GlobalAppConstants.HASH_LENGTH;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * Serves registered URL entities.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UrlRegistrationService {
    private final UrlRepository urlRepository;
    private final AccountToUrlHashesRepository accountToUrlHashesRepository;

    /**
     * Registers (stores) passed URL with some redirect status code and associated with it account identifier.
     * <br />
     * As a result randomly generated hash that will be associated with given URL will be returned.
     *
     * @param accountId account identifier that will be associated with passed URL
     * @param sourceUrl URL that will shorten
     * @param redirectType redirect status code that will be used for redirection
     * @return randomly generated hash that was associated with given URL will be returned
     */
    public String register(final String accountId, final String sourceUrl, final int redirectType) {
        log.info("url:{} with redirect code:{}, will be registered", sourceUrl, redirectType);
        val urlHash = randomAlphanumeric(HASH_LENGTH);
        log.info("requested source url:{}, was shorted to hash:{}", sourceUrl, urlHash);
        RegisteredUrl registeredUrl = RegisteredUrl.builder()
                .sourceUrl(sourceUrl)
                .urlHash(urlHash)
                .redirectType(redirectType)
                .build();
        log.debug("registered url:{} object will be stored with corresponding account id:{}", registeredUrl, accountId);
        urlRepository.insert(registeredUrl);
        accountToUrlHashesRepository.insert(accountId, urlHash);
        return urlHash;
    }
}
