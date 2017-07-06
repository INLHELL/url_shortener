package com.example.urlshortener.service;

import com.example.urlshortener.dao.UrlRepository;
import com.example.urlshortener.model.RegisteredUrl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Manages redirect process.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UrlRedirectService {
    private final UrlRepository urlRepository;

    /**
     * Finds registered URL based on associated URL hash, if URL will be found, usage counter for this URL will be incremented.
     *
     * @param urlHash URL hash
     * @return registered URL associated with passed URL hash wrapped to {@code Optional}, empty if no registered URL will be found
     */
    public Optional<RegisteredUrl> findRegisteredUrl(final String urlHash) {
        log.info("trying to find source URL by given URL hash: {}", urlHash);
        val optionalRegisteredUrl = urlRepository.findByHash(urlHash);
        log.info("source URL was found: {} by given url hash: {}", optionalRegisteredUrl.isPresent(), urlHash);
        optionalRegisteredUrl.ifPresent(url -> urlRepository.incrementUsageCounter(urlHash));
        log.info("source url for hash: {}, was successfully found: {}", urlHash, optionalRegisteredUrl.isPresent());
        return optionalRegisteredUrl;
    }
}
