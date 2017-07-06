package com.example.urlshortener.data.store;

import com.example.urlshortener.model.RegisteredUrl;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data store that keeps object of {@link RegisteredUrl} class.
 */
@Component
public class RegisteredUrlDataStore implements DataStore<RegisteredUrl> {

    private Map<String, RegisteredUrl> dataStore = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public RegisteredUrl insert(final RegisteredUrl entity) {
        dataStore.put(entity.getUrlHash(), entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(final String shortUrl) {
        return dataStore.containsKey(shortUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegisteredUrl find(final String shortUrl) {
        return dataStore.get(shortUrl);
    }
}
