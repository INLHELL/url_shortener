package com.example.urlshortener.data.store;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Collections.emptySet;

/**
 * Data store that keeps pairs of account identifier and set of associated with it url hashes.
 */
@Component
@ThreadSafe
public class AccountToUrlsDataStore implements DataStore<Pair<String, Set<String>>> {
    private Map<String, Set<String>> dataStore = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<String, Set<String>> insert(final Pair<String, Set<String>> accountIdToUrl) {
        readWriteLock.writeLock().lock();
        try {
            dataStore.put(accountIdToUrl.getKey(), accountIdToUrl.getValue());
            return accountIdToUrl;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(final String accountId) {
        readWriteLock.readLock().lock();
        try {
            return dataStore.containsKey(accountId);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<String, Set<String>> find(final String accountId) {
        readWriteLock.readLock().lock();
        try {
            return Pair.of(accountId, dataStore.getOrDefault(accountId, emptySet()));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Inserts new pair - account identifier with set of URL hash or updates existing set of URL hashes by adding new URL hash
     *
     * @param accountId account identifier that will be inserted or updated
     * @param urlHash URL hash that will be added to existing set or will be added as first element to newly created set
     */
    public void upsert(final String accountId, final String urlHash) {
        readWriteLock.writeLock().lock();
        try {
            if (dataStore.containsKey(accountId)) {
                dataStore.get(accountId).add(urlHash);
            } else {
                dataStore.put(accountId, Sets.newHashSet(urlHash));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
