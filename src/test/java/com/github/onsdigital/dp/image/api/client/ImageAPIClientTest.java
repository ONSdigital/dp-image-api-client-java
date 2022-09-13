package com.github.onsdigital.dp.image.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.onsdigital.dp.image.api.client.exception.*;
import com.github.onsdigital.dp.image.api.client.model.Images;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageAPIClientTest {
    private static final String SERVICE_TOKEN_HEADER_NAME = "Authorization";
    private static final String IMAGE_API_URL = "http://imageapi:1234";
    private static final String SERVICE_AUTH_TOKEN = "67856";
    private static final String IMAGE_ID = "321";
    private static final String COLLECTION_ID = "col123";

    @Test
    public void testImageAPI_invalidURI() {

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);

        // Given an invalid URI
        String invalidURI = "{{}}";

        // When a new ImageAPIClient is created
        // Then the expected exception is thrown
        assertThrows(URISyntaxException.class,
                () -> new ImageAPIClient(invalidURI, SERVICE_AUTH_TOKEN, mockHttpClient));
    }

    @Test
    public void testImageAPI_getImages() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock images response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_OK);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        Images mockImagesResponse = mockImagesResponse(mockHttpResponse);

        // When getImages is called without a collection ID
        Images actualImages = imageAPIClient.getImages(null);

        assertNotNull(actualImages);

        HttpRequestBase httpRequest = captureHttpRequest(mockHttpClient);

        // Then no query params are in the URI
        assertNull(httpRequest.getURI().getQuery());

        // Then the request should contain the service token header
        String actualServiceToken = httpRequest.getFirstHeader(SERVICE_TOKEN_HEADER_NAME).getValue();
        assertEquals(SERVICE_AUTH_TOKEN, actualServiceToken);

        // Then the response should be whats returned from the image API
        assertEquals(mockImagesResponse.getCount(), actualImages.getCount());
        assertEquals(mockImagesResponse.getTotalCount(), actualImages.getTotalCount());
        assertEquals(mockImagesResponse.getLimit(), actualImages.getLimit());
        assertEquals(mockImagesResponse.getOffset(), actualImages.getOffset());
    }

    @Test
    public void testImageAPI_getImages_withCollectionId() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock images response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_OK);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        Images mockImagesResponse = mockImagesResponse(mockHttpResponse);

        // When getImages is called with a collection ID
        Images actualImages = imageAPIClient.getImages(COLLECTION_ID);

        assertNotNull(actualImages);

        HttpRequestBase httpRequest = captureHttpRequest(mockHttpClient);

        // Then query params in the URI contain the required collection ID
        assertTrue(httpRequest.getURI().getQuery().contains("collection_id="+COLLECTION_ID));

        // Then the request should contain the service token header
        String actualServiceToken = httpRequest.getFirstHeader(SERVICE_TOKEN_HEADER_NAME).getValue();
        assertEquals(SERVICE_AUTH_TOKEN, actualServiceToken);

        // Then the response should be whats returned from the image API
        assertEquals(mockImagesResponse.getCount(), actualImages.getCount());
        assertEquals(mockImagesResponse.getTotalCount(), actualImages.getTotalCount());
        assertEquals(mockImagesResponse.getLimit(), actualImages.getLimit());
        assertEquals(mockImagesResponse.getOffset(), actualImages.getOffset());
    }

    @Test
    public void testImageAPI_getImages_internalError() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageClient = getImageClient(mockHttpClient);

        // Given a request to the image API that returns a 500
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImages is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageClient.getImages(COLLECTION_ID));
    }

    @Test
    public void testImageAPI_getImages_unauthorised() throws Exception {

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageClient = getImageClient(mockHttpClient);

        // Given a request to the image API that returns a 401
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_UNAUTHORIZED);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImages is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageClient.getImages(COLLECTION_ID));
    }

    @Test
    public void testImageAPI_publishImage() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_NO_CONTENT);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        imageAPIClient.publishImage(IMAGE_ID);

        HttpRequestBase httpRequest = captureHttpRequest(mockHttpClient);

        // Then the request should contain the service token header
        String actualServiceToken = httpRequest.getFirstHeader(SERVICE_TOKEN_HEADER_NAME).getValue();
        assertEquals(SERVICE_AUTH_TOKEN, actualServiceToken);
    }

    @Test
    public void testImageAPI_publishImage_invalidRequest() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_BAD_REQUEST);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(IMAGE_ID));
    }

    @Test
    public void testImageAPI_publishImage_unauthorised() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the dataset API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_UNAUTHORIZED);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(IMAGE_ID));
    }

    @Test
    public void testImageAPI_publishImage_forbidden() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_FORBIDDEN);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(IMAGE_ID));
    }

    @Test
    public void testImageAPI_publishImage_imageNotFound() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_NOT_FOUND);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(IMAGE_ID));
    }

    @Test
    public void testImageAPI_publishImage_internalError() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When publishImage is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(IMAGE_ID));
    }

    private ImageClient getImageClient(CloseableHttpClient mockHttpClient) throws URISyntaxException {
        return new ImageAPIClient(IMAGE_API_URL, SERVICE_AUTH_TOKEN, mockHttpClient);
    }

    private Images mockImagesResponse(CloseableHttpResponse mockHttpResponse) throws JsonProcessingException, UnsupportedEncodingException {
        Images responseBody = new Images();
        responseBody.setCount(1);
        responseBody.setTotalCount(20);
        responseBody.setLimit(10);
        responseBody.setOffset(0);
        MockHttp.responseBody(mockHttpResponse, responseBody);

        return responseBody;
    }

    private HttpRequestBase captureHttpRequest(CloseableHttpClient mockHttpClient) throws IOException {
        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        verify(mockHttpClient).execute(requestCaptor.capture());
        return requestCaptor.getValue();
    }
}
