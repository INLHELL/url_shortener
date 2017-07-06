package com.example.urlshortener.dao;

import com.example.urlshortener.data.store.DataStore;
import com.example.urlshortener.model.RegisteredUrl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Provides method for accessing and storing URLs passed by user.
 */
@Repository
@AllArgsConstructor
@Slf4j
public class UrlRepository {

    private DataStore<RegisteredUrl> registeredUrlDataStore;

    /**
     * Insert registered URL object to the data store, after this URL considered as registered.
     *
     * @param registeredUrl URL that will be inserted
     */
    public void insert(final RegisteredUrl registeredUrl) {
        log.debug("registered url object:{} will be stored at the data store", registeredUrl);
        registeredUrlDataStore.insert(registeredUrl);
    }

    /**
     * Finds registered URL by associated with it hash in the data store.
     *
     * @param urlHash associated with some URL
     * @return object of class {@link RegisteredUrl} that associated with given hash or empty if no object was found
     */
    public Optional<RegisteredUrl> findByHash(final String urlHash) {
        log.debug("url hash:{} will be found at the data store", urlHash);
        return registeredUrlDataStore.exists(urlHash) ? of(registeredUrlDataStore.find(urlHash)) : empty();
    }

    /**
     * Increments usage (redirects) counter for provided URL hash.
     *
     * @param urlHash associated with some URL that was used for redirection
     */
    public void incrementUsageCounter(final String urlHash) {
        log.debug("usage counter will be incremented for url hash:{}", urlHash);
        registeredUrlDataStore.find(urlHash).incrementUsageCounter();
    }
}
