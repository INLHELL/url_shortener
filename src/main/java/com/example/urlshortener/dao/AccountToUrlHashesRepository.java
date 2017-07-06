package com.example.urlshortener.dao;

import com.example.urlshortener.data.store.AccountToUrlsDataStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Provides method for accessing and storing pairs of account identifiers with corresponding URL hashes.
 */
@Repository
@AllArgsConstructor
@Slf4j
public class AccountToUrlHashesRepository {
    private AccountToUrlsDataStore accountToUrlsDataStore;

    /**
     * Inserts account identifier with URL hash if this account identifier wasn't exist before,
     * otherwise URL hash will be added to other URL hashes associated with given account identifier.
     *
     * @param accountId account identifier
     * @param urlHash URL hash that will be associated with account identifier
     */
    public void insert(final String accountId, final String urlHash) {
        log.debug("saving account id:{} with corresponding url hash:{}", accountId, urlHash);
        accountToUrlsDataStore.upsert(accountId, urlHash);
    }

    /**
     * Selects set of URL hashes associated with given account identifier.
     *
     * @param accountId account identifier
     * @return set of URL hashes associated with given account identifier or empty set if no URLs were associated with given identifier
     */
    public Set<String> selectUrlsById(final String accountId) {
        log.debug("selecting url hash by account id:{}", accountId);
        return accountToUrlsDataStore.find(accountId).getValue();
    }
}
