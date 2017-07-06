package com.example.urlshortener.controller;

import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.RegisteredUrl;
import com.example.urlshortener.service.UrlRedirectService;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class RedirectControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private UrlRedirectService urlRedirectService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void redirect() throws Exception {
        String sourceUrl = "http://yandex.ru";
        String urlHash = "HaSh1234";
        when(urlRedirectService.findRegisteredUrl(eq(urlHash))).thenReturn(of(
                RegisteredUrl
                        .builder()
                        .sourceUrl(sourceUrl)
                        .redirectType(FOUND.value())
                        .urlHash(urlHash)
                        .build()));
        mockMvc
                .perform(get("/{urlHash}", urlHash))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION, sourceUrl));
    }

    @Test
    public void redirect_urlNotFound() throws Exception {
        String urlHash = "HaSh1234";
        when(urlRedirectService.findRegisteredUrl(eq(urlHash))).thenReturn(empty());
        mockMvc
                .perform(get("/{urlHash}", urlHash))
                .andExpect(status().isNotFound());
    }
}