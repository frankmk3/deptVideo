package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.dto.VideoSearchDetail;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage TheMovieDbRestClient requests.
 */
@Log4j2
@Service
public class TheMovieDbRestClient extends RemoteRestService {

    private static final String QUERY = "query";
    private static final String API_KEY = "api_key";

    @Setter
    @Value("${themoviedb.search.host}")
    private String searchHost;

    @Setter
    @Value("${themoviedb.video.info.host}")
    private String videoInfoHost;

    @Setter
    @Value("${themoviedb.image.host}")
    private String imageHost;

    @Setter
    @Value("${template.themoviedb.video.to.video.details}")
    private String templatVideoToVideoDetails;

    @Setter
    @Value("${themoviedb.secret}")
    private String secret;

    @Autowired
    public TheMovieDbRestClient(MessagesUtility messagesUtility, RestTemplate restTemplate, ObjectMapper objectMapper, TemplateUtility templateUtility) {
        super(messagesUtility, restTemplate, objectMapper, templateUtility);
    }

    /**
     * Search videos information from TheMovieDB api
     */
    public Map search(String q, @Nullable Map<String, Object> parameters) throws QueryException, EntityNotFoundException, TooManyRequestException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(searchHost);
        uriBuilder.queryParam(QUERY, q.replace(' ', '-'));
        uriBuilder.queryParam(API_KEY, secret);
        if (parameters != null) {
            parameters.forEach(uriBuilder::replaceQueryParam);
        }

        String errorMessageKey = "exception.tmdb.error.template.search";
        return exchange(uriBuilder.toUriString(), HttpMethod.GET, HttpStatus.OK, errorMessageKey, null);
    }

    /**
     * Get details from specific video info.
     */
    @Cacheable(value = "cache", key = "#id")
    public Map getVideoDetails(String id) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(videoInfoHost);
            uriBuilder.pathSegment(id);
            uriBuilder.queryParam(API_KEY, secret);
            uriBuilder.queryParam("append_to_response", "videos");
            String errorMessageKey = "exception.tmdb.error.template.get";
            return exchange(uriBuilder.toUriString(), HttpMethod.GET, HttpStatus.OK, errorMessageKey, null);
        } catch (EntityNotFoundException | QueryException | TooManyRequestException e) {
            log.error(e.getMessage(), e);
        }
        return new HashMap();
    }

    /**
     * Transform the object from the TheMovieDb response to standard VideoSearchDetail
     */
    @Cacheable(value = "cache", key = "#value.get('id')")
    public VideoSearchDetail enhanceVideoDetails(Map value) {
        Map result = objectMapper.convertValue(value, Map.class);
        VideoSearchDetail videoSearchDetail = null;
        try {
            result.put("details", getVideoDetails(String.valueOf(value.get("id"))));
            String values = objectMapper.writeValueAsString(result);
            result = objectMapper.readValue(values.replace("\"/", "\"" + imageHost), Map.class);
            Map<String, Object> parameters = new HashMap();
            parameters.put("info", result);
            parameters.put("objectMapper", objectMapper);
            String transformedJson = templateUtility.parseTemplateAsString(templatVideoToVideoDetails, parameters);
            videoSearchDetail = objectMapper.readValue(transformedJson, VideoSearchDetail.class);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
        }
        return videoSearchDetail;
    }

}
