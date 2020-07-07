package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "totalPages", "totalElements", "numberOfElements", "size", "number", "content" })
public class PaginatedResponse<T> {

    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int numberOfElements;
    private int size;
    private int number;
}