package com.github.onsdigital.dp.image.api.client.exception;

public abstract class ImageAPIException extends Exception {

    public ImageAPIException(String message) {
        super(message);
    }

    public ImageAPIException() {}
}
