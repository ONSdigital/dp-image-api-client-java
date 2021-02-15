package com.github.onsdigital.dp.image.api.client.exception;

/**
 *  ImageAPI Exception
 */
public class ImageAPIException extends Exception {
    private int code;

    public ImageAPIException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ImageAPIException() {
    }

    public int getCode() {
        return code;
    }
}
