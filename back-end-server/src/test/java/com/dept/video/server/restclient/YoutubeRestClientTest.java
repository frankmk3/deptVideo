package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.dto.VideoInfo;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeRestClientTest extends RestClientTest {

    private static final String SEARCH_HOST = "https://www.googleapis.com/youtube/v3/search";
    private static final String VIDEO_HOST = "https://www.googleapis.com/youtube/v3/videos";

    private YoutubeRestClient youtubeRestClient;

    @Before
    public void init() {
        messagesUtility = Mockito.mock(MessagesUtility.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        templateUtility = Mockito.mock(TemplateUtility.class);
        youtubeRestClient = new YoutubeRestClient(messagesUtility, restTemplate, objectMapper, templateUtility);
        youtubeRestClient.setSearchHost(SEARCH_HOST);
        youtubeRestClient.setVideoHost(VIDEO_HOST);
        youtubeRestClient.setTemplateVideoToVideoInfo("video.to.video.info.ftl");
    }

    @Test
    public void whenQueryIsPresentQueryArgumentIsSend() throws EntityNotFoundException, QueryException, TooManyRequestException {
        String query = "youtube_video";
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?q=%s&part&key", SEARCH_HOST, query), response);

        Map search = youtubeRestClient.search(query, null);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void whenParameterOverwriteIsSendIsPresentInTheQueryArguments() throws EntityNotFoundException, QueryException, TooManyRequestException {
        String query = "youtube_video";
        Map<String, Object> parameters = new HashMap<>();
        String apiKeyValue = "api-key-value";
        parameters.put("key", apiKeyValue);
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?q=%s&part&key=%s", SEARCH_HOST, query, apiKeyValue), response);

        Map search = youtubeRestClient.search(query, parameters);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void searchById() throws EntityNotFoundException, QueryException, TooManyRequestException {
        List<String> ids = Arrays.asList("id1");
        Map<String, Object> parameters = new HashMap<>();
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?id=%s&part&key", VIDEO_HOST, String.join(",", ids)), response);

        Map search = youtubeRestClient.searchById(ids, parameters);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void enhanceVideoInfo() throws IOException, TemplateException {
        VideoInfo videoInfo = new VideoInfo();
        Map<String, Object> parameters = new HashMap<>();
        String templateResult = "{\"name\":\"name value\"}";
        Mockito.when(templateUtility.parseTemplateAsString(ArgumentMatchers.anyString(), ArgumentMatchers.isA(Map.class))).thenReturn(templateResult);
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq(templateResult), ArgumentMatchers.isA(Class.class))).thenReturn(videoInfo);

        VideoInfo videoInfoResult = youtubeRestClient.enhanceVideoInfo(parameters);

        Assert.assertEquals(videoInfo, videoInfoResult);
    }

}