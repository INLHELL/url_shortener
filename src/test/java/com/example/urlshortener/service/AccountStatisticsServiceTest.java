package com.example.urlshortener.service;

import com.example.urlshortener.dao.AccountToUrlHashesRepository;
import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.RegisteredUrl;
import com.google.common.collect.ImmutableSet;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
public class AccountStatisticsServiceTest {
    @MockBean
    private AccountToUrlHashesRepository accountToUrlHashesRepository;

    @MockBean
    private UrlRepository urlRepository;

    @Autowired
    @InjectMocks
    private AccountStatisticsService accountStatisticsService;

    @Test
    public void retrieveStatistics() throws Exception {
        String accountId = "123";
        String urlHash = "HaSh1234";
        String sourceUrl = "http://yandex.ru";

        when(accountToUrlHashesRepository.selectUrlsById(accountId)).thenReturn(ImmutableSet.of(urlHash));
        when(urlRepository.findByHash(urlHash)).thenReturn(of(RegisteredUrl
                .builder()
                .redirectType(MOVED_PERMANENTLY.value())
                .urlHash(urlHash)
                .sourceUrl(sourceUrl)
                .build()));
        Map<String, Integer> statistics = accountStatisticsService.retrieveStatistics(accountId);

        MatcherAssert.assertThat(statistics, hasEntry(sourceUrl, 0));
        MatcherAssert.assertThat(statistics.values(), hasSize(1));
    }

    @Test
    public void retrieveStatistics_noUrlHashesSelected() throws Exception {
        String accountId = "123";

        when(accountToUrlHashesRepository.selectUrlsById(accountId)).thenReturn(emptySet());
        Map<String, Integer> statistics = accountStatisticsService.retrieveStatistics(accountId);

        MatcherAssert.assertThat(statistics.values(), hasSize(0));
    }


    @Test
    public void retrieveStatistics_noUrlFound() throws Exception {
        String accountId = "123";
        String urlHash = "HaSh1234";

        when(accountToUrlHashesRepository.selectUrlsById(accountId)).thenReturn(ImmutableSet.of(urlHash));
        when(urlRepository.findByHash(urlHash)).thenReturn(empty());
        Map<String, Integer> statistics = accountStatisticsService.retrieveStatistics(accountId);

        MatcherAssert.assertThat(statistics.values(), hasSize(0));
    }
}