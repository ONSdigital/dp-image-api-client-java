package com.github.onsdigital.dp.image.api.client;

import com.github.onsdigital.dp.image.api.client.exception.ImageAPIException;
import com.github.onsdigital.dp.image.api.client.model.Images;

import java.io.Closeable;
import java.io.IOException;

public interface ImageClient extends Closeable {

    /**
     * Get a collection of images for the given collectionId.
     */
    Images getImagesWithCollectionId(String collectionID)  throws IOException, ImageAPIException;

    /**
     * Publish the image for the given image ID.
     */
    void publishImage(String imageId) throws IOException, ImageAPIException;

}
