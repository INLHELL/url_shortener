package com.example.urlshortener.service;

import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.RegisteredUrl;
import com.google.common.collect.ImmutableSet;
import lombok.val;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
public class UrlRedirectServiceTest {
    @MockBean
    private UrlRepository urlRepository;

    @Autowired
    @InjectMocks
    private UrlRedirectService urlRedirectService;

    @Test
    public void findRegisteredUrl() throws Exception {
        String urlHash = "HaSh1234";
        String sourceUrl = "http://yandex.ru";

        val expectedRegisteredUrl = RegisteredUrl
                .builder()
                .sourceUrl(sourceUrl)
                .urlHash(urlHash)
                .redirectType(302)
                .build();
        when(urlRepository.findByHash(urlHash)).thenReturn(of(expectedRegisteredUrl));

        Optional<RegisteredUrl> registeredUrl = urlRedirectService.findRegisteredUrl(urlHash);

        MatcherAssert.assertThat(registeredUrl.isPresent(), is(true));
        MatcherAssert.assertThat(registeredUrl.get(), is(expectedRegisteredUrl));

        verify(urlRepository).incrementUsageCounter(urlHash);
    }

    @Test
    public void findRegisteredUrl_urlNotFound() throws Exception {
        String urlHash = "HaSh1234";
        when(urlRepository.findByHash(urlHash)).thenReturn(empty());

        Optional<RegisteredUrl> registeredUrl = urlRedirectService.findRegisteredUrl(urlHash);

        MatcherAssert.assertThat(registeredUrl.isPresent(), is(false));
        verify(urlRepository, never()).incrementUsageCounter(urlHash);
    }
}