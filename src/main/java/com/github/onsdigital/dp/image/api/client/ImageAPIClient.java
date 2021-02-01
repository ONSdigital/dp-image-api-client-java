package com.github.onsdigital.dp.image.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.dp.image.api.client.exception.*;
import com.github.onsdigital.dp.image.api.client.model.Images;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class ImageAPIClient implements ImageClient {

    private final URI imageAPIURL;
    private final String serviceAuthToken;

    private final CloseableHttpClient client;

    private static final String serviceTokenHeaderName = "Authorization";
    private static final ObjectMapper json = new ObjectMapper();

    /**
     * Create a new instance of ImageAPIClient
     *
     * @param imageAPIURL      - The URL of the image API
     * @param serviceAuthToken - The authentication token for the image API
     * @param client           - The HTTP client to use internally
     */
    public ImageAPIClient(String imageAPIURL,
                          String serviceAuthToken,
                          CloseableHttpClient client) throws URISyntaxException {

        this.imageAPIURL = new URI(imageAPIURL);
        this.client = client;
        this.serviceAuthToken = serviceAuthToken;
    }

    public ImageAPIClient(String imageAPIURL, String serviceAuthToken) throws URISyntaxException {
        this(imageAPIURL, serviceAuthToken, createDefaultHttpClient());
    }

    private static CloseableHttpClient createDefaultHttpClient() {
        return HttpClients.custom().setServiceUnavailableRetryStrategy(new RetryStrategy()).build();
    }

    /**
     *  Get a collection of images for the given collection ID.
     * @param collectionID
     * @return
     * @throws IOException
     * @throws ImageAPIException
     */
    @Override
    public Images getImagesWithCollectionId(String collectionID) throws IOException, ImageAPIException {

        String path = "/images?collection_id=" + collectionID;
        URI uri = imageAPIURL.resolve(path);

        HttpGet req = new HttpGet(uri);
        req.addHeader(serviceTokenHeaderName, serviceAuthToken);

        try (CloseableHttpResponse resp = executeRequest(req)) {
            int statusCode = resp.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_OK:
                    return parseResponseBody(resp, Images.class);
                default:
                    throw new UnexpectedResponseException(
                            formatErrResponse(req, resp), resp.getStatusLine().getStatusCode());
            }
        }
    }

    @Override
    public void publishImage(String imageId) throws IOException, ImageAPIException {

        validateImageID(imageId);

        String path = "/images/" + imageId + "/publish";
        URI uri = imageAPIURL.resolve(path);

        HttpPost req = new HttpPost(uri);
        req.addHeader(serviceTokenHeaderName, serviceAuthToken);

        try (CloseableHttpResponse resp = executeRequest(req)) {
            int statusCode = resp.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_NO_CONTENT:
                    return;
                default:
                    validate200ResponseCode(req, resp);
            }
        }
    }

    private void validate200ResponseCode(HttpRequestBase httpRequest, CloseableHttpResponse response)
            throws ImageNotFoundException, UnexpectedResponseException, UnauthorisedException, ForbiddenException {
        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                return;
            case HttpStatus.SC_FORBIDDEN:
                throw new ForbiddenException();
            case HttpStatus.SC_NOT_FOUND:
                throw new ImageNotFoundException(formatErrResponse(httpRequest, response));
            case HttpStatus.SC_UNAUTHORIZED:
                throw new UnauthorisedException();
            default:
                throw new UnexpectedResponseException(
                        formatErrResponse(httpRequest, response), response.getStatusLine().getStatusCode());
        }
    }

    private void validateImageID(String imageID) {
        Args.check(isNotEmpty(imageID), "an image id must be provided.");
    }

    private static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    private <T> T parseResponseBody(CloseableHttpResponse response, Class<T> type) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return json.readValue(responseString, type);
    }

    private String formatErrResponse(HttpRequestBase httpRequest, CloseableHttpResponse response) {
        return String.format("the image api returned a %s response for %s",
                response.getStatusLine().getStatusCode(),
                httpRequest.getURI());
    }

    private CloseableHttpResponse executeRequest(HttpUriRequest req) throws IOException {
        info().beginHTTP(req).log("executing image-api request");
        CloseableHttpResponse resp = client.execute(req);
        info().endHTTP(req, resp).log("execute image-api request compeleted");
        return resp;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
