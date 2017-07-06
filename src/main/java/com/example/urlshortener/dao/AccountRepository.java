package com.example.urlshortener.dao;

import com.example.urlshortener.data.store.DataStore;
import com.example.urlshortener.model.Account;
import com.example.urlshortener.data.store.AccountDataStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Provides method for accessing and storing user account in the data store.
 */
@Repository
@Slf4j
@ThreadSafe
public class AccountRepository {
    private DataStore<Account> accountDataStore;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public AccountRepository(final DataStore<Account> accountDataStore) {
        this.accountDataStore = accountDataStore;
    }

    /**
     * Inserts new user if it wasn't already presented in data store.
     *
     * @param accountId user account identifier
     * @param newAccountCreator function for creating new account with passed identifier if it wasn't exist before
     * @return empty {@code Optional} if passed account identifier already exists or newly create instance of {@link Account} class wrapped with {@code Optional}
     */
    public Optional<Account> insertIfNotPresent(final String accountId, final Function<String, Account> newAccountCreator) {
        readWriteLock.writeLock().lock();
        try {
            log.debug("insert account id:{} if it's not already presented in data store", accountId);
            return !accountDataStore.exists(accountId) ? of(accountDataStore.insert(newAccountCreator.apply(accountId))) : empty();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Finds account in data store by passed account identifier.
     *
     * @param accountId user account identifier
     * @return empty {@code Optional} if passed account identifier doesn't exist in data store or instance of {@link Account} class wrapped with {@code Optional}
     */
    public Optional<Account> findById(final String accountId) {
        readWriteLock.readLock().lock();
        try {
            log.debug("find account by id: {} if it's not already presented in data store", accountId);
            return accountDataStore.exists(accountId) ? of(accountDataStore.find(accountId)) : empty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
