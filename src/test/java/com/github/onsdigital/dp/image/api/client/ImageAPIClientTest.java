package com.github.onsdigital.dp.image.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImageAPIClientTest {

    private static final ObjectMapper json = new ObjectMapper();

    private static final String serviceTokenHeaderName = "Authorization";

    private static final String imageAPIURL = "";
    private static final String serviceAuthToken = "67856";
    private static final String instanceID = "123";
    private static final String imageID = "321";
    private static final String collectionID = "col123";
    private static final String imageTitle = "the image title";
    private static final String edition = "current";
    private static final String version = "1";

    @Test
    public void testImageAPI_invalidURI() {

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);

        // Given an invalid URI
        String invalidURI = "{{}}";

        // When a new ImageAPIClient is created
        // Then the expected exception is thrown
        assertThrows(URISyntaxException.class,
                () -> new ImageAPIClient(invalidURI, serviceAuthToken, mockHttpClient));
    }

    @Test
    public void testImageAPI_getImagesWithCollectionId() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock images response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_OK);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        Images mockImagesResponse = mockImagesResponse(mockHttpResponse);

        // When getImagesWithCollectionId is called
        Images actualImages = imageAPIClient.getImagesWithCollectionId(collectionID);

        assertNotNull(actualImages);

        HttpRequestBase httpRequest = captureHttpRequest(mockHttpClient);

        // Then the request should contain the service token header
        String actualServiceToken = httpRequest.getFirstHeader(serviceTokenHeaderName).getValue();
        assertThat(actualServiceToken).isEqualTo(serviceAuthToken);

        // Then the response should be whats returned from the image API
        assertThat(actualImages.getCount()).isEqualTo(mockImagesResponse.getCount());
        assertThat(actualImages.getTotalCount()).isEqualTo(mockImagesResponse.getTotalCount());
        assertThat(actualImages.getLimit()).isEqualTo(mockImagesResponse.getLimit());
        assertThat(actualImages.getOffset()).isEqualTo(mockImagesResponse.getOffset());
    }

    @Test
    public void testImageAPI_getImagesWithCollectionId_internalError() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageClient = getImageClient(mockHttpClient);

        // Given a request to the image API that returns a 500
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageClient.getImagesWithCollectionId(collectionID));
    }

    @Test
    public void testImageAPI_getImagesWithCollectionId_unauthorised() throws Exception {

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageClient = getImageClient(mockHttpClient);

        // Given a request to the image API that returns a 401
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_UNAUTHORIZED);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageClient.getImagesWithCollectionId(collectionID));
    }

    @Test
    public void testImageAPI_publishImage() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_NO_CONTENT);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        imageAPIClient.publishImage(imageID);

        HttpRequestBase httpRequest = captureHttpRequest(mockHttpClient);

        // Then the request should contain the service token header
        String actualServiceToken = httpRequest.getFirstHeader(serviceTokenHeaderName).getValue();
        assertThat(actualServiceToken).isEqualTo(serviceAuthToken);
    }

    @Test
    public void testImageAPI_publishImage_invalidRequest() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_BAD_REQUEST);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(imageID));
    }

    @Test
    public void testImageAPI_publishImage_unauthorised() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the dataset API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_UNAUTHORIZED);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(imageID));
    }

    @Test
    public void testImageAPI_publishImage_forbidden() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_FORBIDDEN);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(imageID));
    }

    @Test
    public void testImageAPI_publishImage_imageNotFound() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_NOT_FOUND);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(imageID));
    }

    @Test
    public void testImageAPI_publishImage_internalError() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        ImageClient imageAPIClient = getImageClient(mockHttpClient);

        // Given a mock image publishing response from the image API
        CloseableHttpResponse mockHttpResponse = MockHttp.response(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockHttpClient.execute(any(HttpRequestBase.class))).thenReturn(mockHttpResponse);

        // When getImagesWithCollectionId is called
        // Then the expected exception is thrown
        assertThrows(ImageAPIException.class,
                () -> imageAPIClient.publishImage(imageID));
    }

    private ImageClient getImageClient(CloseableHttpClient mockHttpClient) throws URISyntaxException {
        return new ImageAPIClient(imageAPIURL, serviceAuthToken, mockHttpClient);
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
