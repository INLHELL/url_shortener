package com.example.urlshortener.controller;


import com.example.urlshortener.model.Account;
import com.example.urlshortener.service.AccountService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.UNSUCCESSFUL;
import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.SUCCESSFUL;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * This controller is used new user account registration.
 */
@RestController
@Slf4j
@AllArgsConstructor
public class AccountsController {

    private AccountService accountService;

    /**
     * Accepts user's accountId and returns generated password if the accountId was not opened (passed) already.
     *
     * @param accountId user's account identifier
     * @return object of {@link AccountOpeningResponse} class, that contains generated password (if passed accountId wasn't opened before),
     * opening status and short description status
     */
    @PostMapping(
            path = "/account",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public AccountOpeningResponse openAccount(@RequestBody final AccountIdRequest accountId) {
        log.info("/account request processed, account id: {}", accountId.getId());
        val optionalAccount = accountService.openAccount(accountId.id);
        log.info("account id: {} was successfully opened: {}", accountId, optionalAccount.isPresent());
        return optionalAccount.map(AccountOpeningResponse::successful).orElseGet(AccountOpeningResponse::unsuccessful);
    }

    @Value
    private static final class AccountIdRequest {
        @JsonProperty("AccountId")
        private final String id;
    }


    @Value
    @JsonInclude(NON_NULL)
    private static final class AccountOpeningResponse {
        @JsonProperty("success")
        private final String status;
        @JsonProperty("description")
        private final String description;
        @JsonProperty("password")
        private final String password;

        static AccountOpeningResponse successful(final Account userAccount) {
            return new AccountOpeningResponse(SUCCESSFUL.getStatus(), SUCCESSFUL.getDescription(), userAccount.getPassword());
        }

        static AccountOpeningResponse unsuccessful() {
            return new AccountOpeningResponse(UNSUCCESSFUL.getStatus(), UNSUCCESSFUL.getDescription(), null);
        }
    }

    @AllArgsConstructor
    public enum AccountOpeningStatus {
        SUCCESSFUL("true", "Your account is opened"),
        UNSUCCESSFUL("false", "account with that ID already exists");

        @Getter private String status;
        @Getter private String description;
    }

}
