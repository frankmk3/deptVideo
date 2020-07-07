package com.dept.video.server.service;

import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoSearchDetail;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.dept.video.server.restclient.TheMovieDbRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TheMovieDbService implements SearchVideoInformation {

    private final TheMovieDbRestClient theMovieDbRestClient;

    @Autowired
    public TheMovieDbService(TheMovieDbRestClient theMovieDbRestClient) {
        this.theMovieDbRestClient = theMovieDbRestClient;
    }

    @Override
    public PaginatedResponse<VideoSearchDetail> search(String q, Pageable pageable) throws QueryException, EntityNotFoundException, TooManyRequestException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", pageable.getPageNumber() + 1);
        Map search = theMovieDbRestClient.search(q, parameters);
        return transformTheMovieDbResponse(search);
    }

    /**
     * Process each search result and transform to a PaginatedResponse<VideoSearchDetail> object
     */
    private PaginatedResponse<VideoSearchDetail> transformTheMovieDbResponse(Map search) {
        Integer totalResults = (Integer) search.get("total_results");
        int size = 20;
        int totalPages = (Integer) search.get("total_pages");
        int page = (Integer) search.get("page");
        List content = addVideoDetails((List) search.get("results"));
        return PaginatedResponse.builder()
                .size(size)
                .numberOfElements(totalResults)
                .totalElements(totalResults)
                .totalPages(totalPages)
                .number(page - 1)
                .content(content)
                .build();
    }

    public List<VideoSearchDetail> addVideoDetails(List<Map> values) {
        return values.parallelStream().map(theMovieDbRestClient::enhanceVideoDetails
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

}