package com.github.onsdigital.dp.image.api.client.exception;

/**
 *  ImageAPI Exception
 */
public class ImageAPIException extends Exception {
    private int code;

    /**
     * Create a new instance of an ImageAPIException
     *
     * @param message   A string detailing the reason for the exception
     * @param code      The http status code that caused the API exception
     */
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
