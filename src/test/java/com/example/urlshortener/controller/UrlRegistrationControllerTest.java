package com.example.urlshortener.controller;

import com.example.urlshortener.dao.AccountRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.Account;
import com.example.urlshortener.service.UrlRegistrationService;
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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class UrlRegistrationControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private UrlRegistrationService urlRegistrationService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void register() throws Exception {
        when(urlRegistrationService.register(anyString(), anyString(), anyInt())).thenReturn("HaSh1234");
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"http://yandex.ru\", \"redirectType\":302}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost/HaSh1234"));
    }

    @Test
    public void register_noStatusCode() throws Exception {
        when(urlRegistrationService.register(anyString(), anyString(), anyInt())).thenReturn("HaSh1234");
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        String sourceUrl = "http://yandex.ru";
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"" + sourceUrl + "\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost/HaSh1234"));

        verify(urlRegistrationService).register(account.getId(), sourceUrl, 302);
    }

    @Test
    public void register_statusCodeNotAllowed() throws Exception {
        when(urlRegistrationService.register(anyString(), anyString(), anyInt())).thenReturn("HaSh1234");
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"http://yandex.ru\", \"redirectType\":402}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Invalid redirect code"));
    }

    @Test
    public void register_invalidUrl() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"yandex.ru\", \"redirectType\":302}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Invalid URL"));
    }

    @Test
    public void register_urlNotExist() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"redirectType\":302}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.error").value("Invalid URL"));
    }


    @Test
    public void register_authorizationHeaderNotExist() throws Exception {
        mockMvc
                .perform(post("/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"http://yandex.ru\", \"redirectType\":302}")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void register_accountNotExist() throws Exception {
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(empty());
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"http://yandex.ru\", \"redirectType\":302}")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void register_contentBodyNotExist() throws Exception {
        when(urlRegistrationService.register(anyString(), anyString(), anyInt())).thenReturn("HaSh1234");
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}