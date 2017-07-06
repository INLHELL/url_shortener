package com.example.urlshortener.model;

import lombok.Builder;
import lombok.Value;

import java.util.concurrent.atomic.AtomicInteger;

@Value
@Builder
public class RegisteredUrl {
    private final String sourceUrl;
    private final String urlHash;
    private final int redirectType;
    private final AtomicInteger usageCounter = new AtomicInteger(0);

    public void incrementUsageCounter() {
        usageCounter.incrementAndGet();
    }

    public int getUsageCounter() {
        return usageCounter.get();
    }
}
