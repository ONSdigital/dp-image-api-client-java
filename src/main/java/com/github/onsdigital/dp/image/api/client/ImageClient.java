package com.github.onsdigital.dp.image.api.client;

import com.github.onsdigital.dp.image.api.client.exception.ImageAPIException;
import com.github.onsdigital.dp.image.api.client.model.Images;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface representing an Image API Client
 */
public interface ImageClient extends Closeable {

    /**
     * Get a collection of images
     * If the optional collectionId is supplied only images with a matching collection_id are returned.
     *
     * @param collectionID An optional string containing a collectionID to limit the results
     * @return An {@link Images} object containing a list of Image objects
     * @throws IOException
     * @throws ImageAPIException
     */
    Images getImages(String collectionID) throws IOException, ImageAPIException;

    /**
     * Publish the image for the given image ID.
     *
     * @param imageId A string containing the id of a specific image to publish
     * @throws IOException
     * @throws ImageAPIException
     */
    void publishImage(String imageId) throws IOException, ImageAPIException;

}
