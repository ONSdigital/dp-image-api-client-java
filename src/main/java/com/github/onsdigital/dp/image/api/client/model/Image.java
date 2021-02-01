package com.github.onsdigital.dp.image.api.client.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The model of an image as provided by the image API
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonAlias()
    private String collectionId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String state;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String filename;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Licence license;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Links links;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Upload upload;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collection_id) {
        this.collectionId = collection_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Licence getLicense() {
        return license;
    }

    public void setLicense(Licence license) {
        this.license = license;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Licence {
        private String title;
        private String href;
    }

    public static class Links {
        public String self;
        public String downloads;
    }

    public static class Upload {
        public String path;
    }
}
