package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({ "totalPages", "totalElements", "numberOfElements", "size", "number","prevPageToken", "nextPageToken", "content" })
public class YoutubePaginatedResponse extends PaginatedResponse {

    private String nextPageToken;

    private String prevPageToken;

    @Builder(builderMethodName = "youtubePaginatedResponseBuilder")
    public YoutubePaginatedResponse(List content, int totalPages, int totalElements
            , int numberOfElements, int size, int number, String nextPageToken, String prevPageToken) {
        super(content, totalPages, totalElements, numberOfElements, size, number);
        this.nextPageToken = nextPageToken;
        this.prevPageToken = prevPageToken;
    }
}