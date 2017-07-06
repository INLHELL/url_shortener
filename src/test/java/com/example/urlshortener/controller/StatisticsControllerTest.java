package com.example.urlshortener.controller;

import com.example.urlshortener.dao.AccountRepository;
import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.Account;
import com.example.urlshortener.service.AccountStatisticsService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class StatisticsControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private AccountStatisticsService accountStatisticsService;

    @MockBean
    private AccountToUrlHashesRepository accountToUrlHashesRepository;

    @MockBean
    private UrlRepository urlRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void statistics() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(account.getId())).thenReturn(of(account));
        when(accountStatisticsService.retrieveStatistics(eq(account.getId()))).thenReturn(
                ImmutableMap.of("http://yandex.ru", 100, "http://google.com", 200));
        mockMvc
                .perform(get("/statistics/{accountId}", account.getId())
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.['http://yandex.ru']").value(100))
                .andExpect(jsonPath("$.['http://google.com']").value(200));
    }

    @Test
    public void statistics_wrongHttpMethod() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(account.getId())).thenReturn(of(account));
        mockMvc
                .perform(post("/statistics/{accountId}", account.getId())
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isNotImplemented())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.errorCode").value(NOT_IMPLEMENTED.value()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void statistics_anotherAccountIdInPath() throws Exception {
        Account account = new Account("username");
        String anotherAccountId = "anotherAccountId";
        when(accountRepository.findById(account.getId())).thenReturn(of(account));
        when(accountStatisticsService.retrieveStatistics(eq(anotherAccountId))).thenReturn(
                ImmutableMap.of("http://yandex.ru", 100, "http://google.com", 200));
        mockMvc
                .perform(get("/statistics/{accountId}", anotherAccountId)
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.['http://yandex.ru']").value(100))
                .andExpect(jsonPath("$.['http://google.com']").value(200));
    }

    @Test
    public void statistics_noUrlsWereRegistered() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(account.getId())).thenReturn(of(account));
        when(accountToUrlHashesRepository.selectUrlsById(account.getId())).thenReturn(emptySet());
        mockMvc
                .perform(get("/statistics/{accountId}", account.getId())
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    public void statistics_noUrlWasRegisteredButStillNotHashed() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(account.getId())).thenReturn(of(account));
        when(accountToUrlHashesRepository.selectUrlsById(account.getId())).thenReturn(ImmutableSet.of("http://yandex.ru"));
        when(urlRepository.findByHash(anyString())).thenReturn(empty());
        mockMvc
                .perform(get("/statistics/{accountId}", account.getId())
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(0)));
    }
}