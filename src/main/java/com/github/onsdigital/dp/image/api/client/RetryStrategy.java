package com.github.onsdigital.dp.image.api.client;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * Custom implementation of ServiceUnavailableRetryStrategy to retry any HTTP 5xx responses.
 */
public class RetryStrategy implements ServiceUnavailableRetryStrategy {

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
                response.getStatusLine().getStatusCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public long getRetryInterval() {
        return retryIntervalMs;
    }
}
