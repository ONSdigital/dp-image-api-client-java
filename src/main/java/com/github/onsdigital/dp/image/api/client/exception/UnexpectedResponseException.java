package com.github.onsdigital.dp.image.api.client.exception;

public class UnexpectedResponseException extends ImageAPIException {

    private final int responseCode;

    public UnexpectedResponseException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
