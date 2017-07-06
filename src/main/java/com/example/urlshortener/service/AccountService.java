package com.example.urlshortener.service;

import com.example.urlshortener.model.Account;
import com.example.urlshortener.dao.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serves account entities.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepository accountDao;

    /**
     * Creates new accounts with passed account identifier if account with such an identifier wasn't created before.
     *
     * @param accountId account identifier that will be used for creating new object of {@link Account} class
     * @return newly created object of {@link Account} class wrapped with {@code Optional} or empty {@code Optional}
     */
    public Optional<Account> openAccount(final String accountId) {
        log.info("open account id: {}", accountId);
        return accountDao.insertIfNotPresent(accountId, Account::new);
    }
}
