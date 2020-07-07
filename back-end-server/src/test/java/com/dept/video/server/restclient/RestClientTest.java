package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.TemplateUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public abstract class RestClientTest {

    protected RestTemplate restTemplate;
    protected ObjectMapper objectMapper;
    protected TemplateUtility templateUtility;
    protected MessagesUtility messagesUtility;

    protected void mockRestTemplateExchange(String query, ResponseEntity<Map> response) {
        Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(query),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.isA(HttpEntity.class),
                ArgumentMatchers.isA(Class.class))).thenReturn(response);
    }

}