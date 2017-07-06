package com.example.urlshortener.controller;

import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.Account;
import com.example.urlshortener.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.SUCCESSFUL;
import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.UNSUCCESSFUL;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class AccountsControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .build();
    }

    @Test
    public void openAccount() throws Exception {
        when(accountService.openAccount(eq("first"))).thenReturn(Optional.of(new Account("first")));

        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .content("{\"AccountId\":\"first\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.success").value(SUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(SUCCESSFUL.getDescription()))
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    public void openAccount_accountAlreadyExist() throws Exception {
        when(accountService.openAccount(eq("first"))).thenReturn(Optional.empty());

        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .content("{\"AccountId\":\"first\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.success").value(UNSUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(UNSUCCESSFUL.getDescription()));

    }
}