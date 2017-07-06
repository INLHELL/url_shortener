package com.example.urlshortener.data.store;

import com.example.urlshortener.model.Account;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data store that keeps object of {@link Account} class.
 */
@Component
public final class AccountDataStore implements DataStore<Account> {

    private Map<String, Account> dataStore = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Account insert(final Account account) {
        dataStore.put(account.getId(), account);
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(final String id) {
        return dataStore.containsKey(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account find(final String accountId) {
        return dataStore.get(accountId);
    }
}
