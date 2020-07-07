package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.dto.VideoSearchDetail;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TheMovieDbRestClientTest extends RestClientTest {

    private static final String SEARCH_HOST = "https://api.themoviedb.org/3/search/movie";
    private static final String SEARCH_VIDEO_INFO_HOST = "https://api.themoviedb.org/3/movie/";

    private TheMovieDbRestClient theMovieDbRestClient;

    @Before
    public void init() {
        messagesUtility = Mockito.mock(MessagesUtility.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        templateUtility = Mockito.mock(TemplateUtility.class);

        theMovieDbRestClient = new TheMovieDbRestClient(messagesUtility, restTemplate, objectMapper, templateUtility);
        theMovieDbRestClient.setSearchHost(SEARCH_HOST);
        theMovieDbRestClient.setVideoInfoHost(SEARCH_VIDEO_INFO_HOST);
        theMovieDbRestClient.setTemplatVideoToVideoDetails("video.template.ftl");

        Mockito.when(objectMapper.convertValue(ArgumentMatchers.isA(Map.class), ArgumentMatchers.isA(Class.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });

    }

    @Test
    public void whenQueryIsPresentQueryArgumentIsSend() throws EntityNotFoundException, QueryException, TooManyRequestException {
        String query = "movie";
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?query=%s&api_key", SEARCH_HOST, query), response);

        Map search = theMovieDbRestClient.search(query, null);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void whenQueryHasSpaceQueryArgumentQueryReplaceToDash() throws EntityNotFoundException, QueryException, TooManyRequestException {
        String query = "movie search";
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?query=%s&api_key", SEARCH_HOST, query.replace(' ', '-')), response);

        Map search = theMovieDbRestClient.search(query, null);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void whenParameterOverwriteIsSendIsPresentInTheQueryArguments() throws EntityNotFoundException, QueryException, TooManyRequestException {
        String query = "movie";
        Map<String, Object> parameters = new HashMap<>();
        String apiKeyValue = "api-key-value";
        parameters.put("api_key", apiKeyValue);
        HashMap searchResult = new HashMap();
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s?query=%s&api_key=%s", SEARCH_HOST, query, apiKeyValue), response);

        Map search = theMovieDbRestClient.search(query, parameters);

        Assert.assertEquals(search, searchResult);
    }

    @Test
    public void getVideoDetails() throws IOException, TemplateException {
        HashMap searchResult = new HashMap();
        searchResult.put("custom-key", "custom-value");
        String videoId = "video-id";
        ResponseEntity<Map> response = ResponseEntity.ok(searchResult);
        mockRestTemplateExchange(String.format("%s%s?api_key&append_to_response=videos", SEARCH_VIDEO_INFO_HOST, videoId), response);

        Map videoDetails = theMovieDbRestClient.getVideoDetails("video-id");

        Assert.assertEquals(videoDetails, searchResult);
    }

    @Test
    public void enhanceVideoDetails() throws IOException, TemplateException {
        Mockito.when(objectMapper.writeValueAsString(ArgumentMatchers.isA(Map.class))).thenReturn("{}");
        VideoSearchDetail videoSearchDetail = new VideoSearchDetail();
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq("{}"), ArgumentMatchers.isA(Class.class))).thenReturn(new HashMap<>());
        String templateResult = "{\"name\":\"name value\"}";
        Mockito.when(objectMapper.readValue(ArgumentMatchers.eq(templateResult), ArgumentMatchers.isA(Class.class))).thenReturn(videoSearchDetail);
        Mockito.when(templateUtility.parseTemplateAsString(ArgumentMatchers.anyString(), ArgumentMatchers.isA(Map.class))).thenReturn(templateResult);
        String videoId = "video-id";
        HashMap videoInfo = new HashMap();
        videoInfo.put("id", videoId);
        ResponseEntity<Map> response = ResponseEntity.ok(videoInfo);
        mockRestTemplateExchange(String.format("%s%s?api_key&append_to_response=videos", SEARCH_VIDEO_INFO_HOST, videoId), response);

        VideoSearchDetail videoSearchDetailResponse = theMovieDbRestClient.enhanceVideoDetails(videoInfo);

        Assert.assertEquals(videoSearchDetailResponse, videoSearchDetail);
    }

}