package com.dept.video.server.service;

import com.dept.video.server.common.MapUtility;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoInfo;
import com.dept.video.server.dto.YoutubePaginatedResponse;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.dept.video.server.restclient.YoutubeRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YoutubeService implements VideoDetailInformation {

    private static final String FIELDS = "part";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PAGE_TOKEN = "pageToken";
    private static final String ORDER = "order";
    private final YoutubeRestClient youtubeRestClient;

    @Autowired
    public YoutubeService(YoutubeRestClient youtubeRestClient) {
        this.youtubeRestClient = youtubeRestClient;
    }

    public YoutubePaginatedResponse search(String q, Optional<String[]> orders, Optional<String> fields, Pageable pageable, String pageToken) throws QueryException, EntityNotFoundException, TooManyRequestException {
        Map search = youtubeRestClient.search(q, generateParameters(orders, fields, pageable, pageToken));
        return transformYoutubeResponse(search);
    }


    public YoutubePaginatedResponse searchByIds(List<String> ids, Optional<String[]> orders, Optional<String> fields, Pageable pageable, String pageToken) throws QueryException, EntityNotFoundException, TooManyRequestException {
        Map search = youtubeRestClient.searchById(ids, generateParameters(orders, fields, pageable, pageToken));
        return transformYoutubeResponse(search);
    }

    /**
     * Search information of multiple ids.
     */
    @Override
    public PaginatedResponse<VideoInfo> searchByIds(List<String> ids, Pageable pageable) throws QueryException, EntityNotFoundException, TooManyRequestException {
        YoutubePaginatedResponse response = searchByIds(ids, Optional.empty(), Optional.empty(), pageable, null);
        PaginatedResponse.PaginatedResponseBuilder<VideoInfo> builder = PaginatedResponse.builder();
        builder.number(response.getNumber());
        builder.numberOfElements(response.getNumberOfElements());
        builder.size(response.getSize());
        builder.totalPages(response.getTotalPages());
        builder.totalElements(response.getTotalElements());
        builder.content(addVideoDetails(response.getContent()));
        return builder.build();
    }

    /**
     * Transform the search fields into Youtube api query parameters
     */
    private Map<String, Object> generateParameters(Optional<String[]> orders, Optional<String> fields, Pageable pageable, String pageToken) {
        Map<String, Object> parameters = new HashMap<>();
        if (orders.isPresent() && orders.get().length > 0) {
            parameters.put(ORDER, orders.get()[0]);
        }
        if (!StringUtils.isEmpty(pageToken)) {
            parameters.put(PAGE_TOKEN, pageToken);
        }
        fields.ifPresent(s -> parameters.put(FIELDS, s));
        parameters.put(MAX_RESULTS, pageable.getPageSize());
        return parameters;
    }

    /**
     * Generate a YoutubePaginatedResponse object
     */
    private YoutubePaginatedResponse transformYoutubeResponse(Map<String, Object> search) {
        Integer totalResults = (Integer) MapUtility.extractValueFromPath(search, "pageInfo.totalResults");
        Integer size = (Integer) MapUtility.extractValueFromPath(search, "pageInfo.resultsPerPage");
        int totalPages = (int) Math.ceil((double) totalResults / size);
        List<Map> content = (List<Map>) search.get("items");

        return YoutubePaginatedResponse.youtubePaginatedResponseBuilder()
                .nextPageToken((String) search.get("nextPageToken"))
                .prevPageToken((String) search.get("prevPageToken"))
                .size(size)
                .numberOfElements(totalResults)
                .totalPages(totalPages)
                .content(content)
                .build();
    }


    private List<VideoInfo> addVideoDetails(List<Map> values) {
        return values.stream().map(youtubeRestClient::enhanceVideoInfo).collect(Collectors.toList());

    }


}