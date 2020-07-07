package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.dto.VideoInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage Youtube requests.
 */
@Log4j2
@Service
public class YoutubeRestClient extends RemoteRestService {

    private static final String QUERY = "q";
    private static final String ID = "id";
    private static final String FIELDS = "part";
    private static final String API_KEY = "key";

    @Setter
    @Value("${youtube.search.host}")
    private String searchHost;

    @Setter
    @Value("${youtube.video.host}")
    private String videoHost;

    @Setter
    @Value("${youtube.search.default.fields}")
    private String searchDefaultFields;

    @Setter
    @Value("${youtube.video.default.fields}")
    private String videoDefaultFields;

    @Setter
    @Value("${youtube.secret}")
    private String secret;

    @Setter
    @Value("${template.youtube.video.to.video.info}")
    private String templateVideoToVideoInfo;

    @Autowired
    public YoutubeRestClient(MessagesUtility messagesUtility, RestTemplate restTemplate, ObjectMapper objectMapper, TemplateUtility templateUtility) {
        super(messagesUtility, restTemplate, objectMapper, templateUtility);
    }

    /**
     * Search videos from Youtube api
     */
    public Map search(String q, Map<String, Object> parameters) throws QueryException, EntityNotFoundException, TooManyRequestException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(searchHost);
        uriBuilder.queryParam(QUERY, q);
        uriBuilder.queryParam(FIELDS, searchDefaultFields);
        uriBuilder.queryParam(API_KEY, secret);
        if (parameters != null) {
            parameters.forEach(uriBuilder::replaceQueryParam);
        }

        String errorMessageKey = "exception.youtube.error.template.search";
        return exchange(uriBuilder.toUriString(), HttpMethod.GET, HttpStatus.OK, errorMessageKey, null);
    }

    /**
     * Get details from specific video.
     */
    @Cacheable(value = "cache", key = "#ids")
    public Map searchById(List<String> ids, Map<String, Object> parameters) throws QueryException, EntityNotFoundException, TooManyRequestException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(videoHost);
        uriBuilder.queryParam(ID, String.join(",", ids));
        uriBuilder.queryParam(FIELDS, videoDefaultFields);
        uriBuilder.queryParam(API_KEY, secret);
        if (parameters != null) {
            parameters.forEach(uriBuilder::replaceQueryParam);
        }
        String errorMessageKey = "exception.youtube.error.template.get";
        return exchange(uriBuilder.toUriString(), HttpMethod.GET, HttpStatus.OK, errorMessageKey, null);
    }


    /**
     * Transform the object from the Youtube response to standard VideoInfo
     */
    @Cacheable(value = "cache", key = "#value.get('id')")
    public VideoInfo enhanceVideoInfo(Map value) {
        Map result = objectMapper.convertValue(value, Map.class);
        VideoInfo videoInfo = null;
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("video", result);
            parameters.put("objectMapper", objectMapper);
            String transformedJson = templateUtility.parseTemplateAsString(templateVideoToVideoInfo, parameters);
            videoInfo = objectMapper.readValue(transformedJson, VideoInfo.class);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
        }
        return videoInfo;
    }
}
