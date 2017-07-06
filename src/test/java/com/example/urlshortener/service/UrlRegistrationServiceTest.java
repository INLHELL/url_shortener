package com.example.urlshortener.service;

import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.RegisteredUrl;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
public class UrlRegistrationServiceTest {

    @MockBean
    private UrlRepository urlRepository;

    @MockBean
    private AccountToUrlHashesRepository accountToUrlHashesRepository;

    @Autowired
    @InjectMocks
    private UrlRegistrationService urlRegistrationService;

    @Test
    public void findRegisteredUrl() throws Exception {
        val sourceUrl = "http://yandex.ru";
        val accountId = "accountId";

        String urlHash = urlRegistrationService.register(accountId, sourceUrl, 302);
        val expectedRegisteredUrl = RegisteredUrl
                .builder()
                .sourceUrl(sourceUrl)
                .urlHash(urlHash)
                .redirectType(302)
                .build();

        verify(urlRepository).insert(expectedRegisteredUrl);
        verify(accountToUrlHashesRepository).insert(accountId, urlHash);
    }

}