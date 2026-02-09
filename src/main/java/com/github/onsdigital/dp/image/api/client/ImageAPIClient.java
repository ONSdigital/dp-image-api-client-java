package com.github.onsdigital.dp.image.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.dp.image.api.client.exception.*;
import com.github.onsdigital.dp.image.api.client.model.Images;
import org.apache.commons.lang3.StringUtils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Implementation of an client for the Image API
 */
public class ImageAPIClient implements ImageClient {

    private final URI imageAPIURL;
    private final String serviceAuthToken;

    private final CloseableHttpClient client;

    private static final String serviceTokenHeaderName = "Authorization";
    private static final ObjectMapper json = new ObjectMapper();

    /**
     * Create a new instance of ImageAPIClient
     *
     * @param imageAPIURL      The URL of the image API
     * @param serviceAuthToken The authentication token for the image API
     * @param client           The HTTP client to use internally
     */
    public ImageAPIClient(String imageAPIURL,
                          String serviceAuthToken,
                          CloseableHttpClient client) throws URISyntaxException {

        this.imageAPIURL = new URI(imageAPIURL);
        this.client = client;
        this.serviceAuthToken = serviceAuthToken;
    }

    /**
     * Create a new instance of ImageAPIClient with a default Http client
     *
     * @param imageAPIURL      The URL of the image API
     * @param serviceAuthToken The authentication token for the image API
     * @throws URISyntaxException
     */
    public ImageAPIClient(String imageAPIURL, String serviceAuthToken) throws URISyntaxException {
        this(imageAPIURL, serviceAuthToken, createDefaultHttpClient());
    }

    private static CloseableHttpClient createDefaultHttpClient() {
        return HttpClients.custom().setRetryStrategy(new RetryStrategy()).build();
    }

    /**
     * Get a collection of images
     * If the optional collectionId is supplied only images with a matching collection_id are returned.
     *
     * @param collectionID An optional string containing a collectionID to limit the results
     * @return An {@link Images} object containing a list of Image objects
     * @throws IOException
     * @throws ImageAPIException
     */
    @Override
    public Images getImages(String collectionID) throws IOException, ImageAPIException {

        StringBuilder pathBuilder = new StringBuilder("/images");
        if (StringUtils.isNotEmpty(collectionID)) {
            pathBuilder.append("?collection_id=").append(collectionID);
        }

        URI uri = imageAPIURL.resolve(pathBuilder.toString());

        HttpGet req = new HttpGet(uri);
        req.addHeader(serviceTokenHeaderName, serviceAuthToken);

        try (CloseableHttpResponse resp = executeRequest(req)) {
            int statusCode = resp.getCode();

            switch (statusCode) {
                case HttpStatus.SC_OK:
                    return parseResponseBody(resp, Images.class);
                default:
                    throw new ImageAPIException(formatErrResponse(req, resp, HttpStatus.SC_OK), statusCode);
            }
        } catch (ParseException e) {
            throw new ImageAPIException("failed to parse response from image api", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Publish an image by calling the POST /images/{id}/publish endpoint
     *
     * @param imageId A string containing the id of a specific image to publish
     * @throws IOException
     * @throws ImageAPIException
     */
    @Override
    public void publishImage(String imageId) throws IOException, ImageAPIException {

        validateImageID(imageId);

        String path = "/images/" + imageId + "/publish";
        URI uri = imageAPIURL.resolve(path);

        HttpPost req = new HttpPost(uri);
        req.addHeader(serviceTokenHeaderName, serviceAuthToken);

        try (CloseableHttpResponse resp = executeRequest(req)) {
            int statusCode = resp.getCode();

            switch (statusCode) {
                case HttpStatus.SC_NO_CONTENT:
                    return;
                default:
                    throw new ImageAPIException(formatErrResponse(req, resp, HttpStatus.SC_NO_CONTENT), statusCode);
            }
        }
    }

    private void validateImageID(String imageID) {
        Args.check(StringUtils.isNotEmpty(imageID), "an image id must be provided.");
    }

    private <T> T parseResponseBody(CloseableHttpResponse response, Class<T> type) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return json.readValue(responseString, type);
    }

   private String formatErrResponse(HttpUriRequestBase httpRequest, CloseableHttpResponse response, int expectedStatus) {
        int responseCode = response.getCode();

        try {
            String requestURI = httpRequest.getUri().toString();
            return String.format("the image api returned a %s response for %s",
                            responseCode,
                            requestURI);
        } catch (URISyntaxException e) {
            return String.format("the image api returned a %s response for %s",
                responseCode,
                httpRequest.getRequestUri());
        }
    }

    private CloseableHttpResponse executeRequest(HttpUriRequest req) throws IOException {
        info().beginHTTP(req).log("executing image api request");
        CloseableHttpResponse resp = client.execute(req);
        info().endHTTP(req, resp).log("execute image api request completed");
        return resp;
    }

    /**
     * Close the http client used by the ImageAPIClient
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        client.close();
    }
}
