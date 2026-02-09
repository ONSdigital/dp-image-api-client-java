package com.github.onsdigital.dp.image.api.client;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;

import java.io.IOException;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
/**
 * Custom implementation of ServiceUnavailableRetryStrategy to retry any HTTP 5xx responses.
 */
public class RetryStrategy implements HttpRequestRetryStrategy {

    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final int DEFAULT_RETRY_INTERVAL_MS = 20;

    private final int maxRetries;
    private final long retryIntervalMs;

    public RetryStrategy(int maxRetries, long retryIntervalMs) {
        this.maxRetries = maxRetries;
        this.retryIntervalMs = retryIntervalMs;
    }

    public RetryStrategy() {
        this(DEFAULT_MAX_RETRIES, DEFAULT_RETRY_INTERVAL_MS);
    }

    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        return executionCount <= maxRetries &&
                response.getCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }
    
    @Override
    public boolean retryRequest(HttpRequest request, IOException exception, int executionCount, HttpContext context) {
        return executionCount <= maxRetries;
    }

    @Override
    public TimeValue getRetryInterval(HttpResponse response, int executionCount, HttpContext context) {
        return TimeValue.ofMilliseconds(retryIntervalMs);
    }
}
