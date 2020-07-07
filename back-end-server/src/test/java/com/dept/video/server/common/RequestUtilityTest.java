package com.dept.video.server.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RequestUtilityTest {

    private HttpServletRequest request;

    @Before
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<>()));
    }

    @Test
    public void whenHeaderHasParameterReturnsInHeadersAttributes() {
        Map<String, String> customHeader = new HashMap<>();
        String value = "auth-token";
        customHeader.put(HttpHeaders.AUTHORIZATION, value);
        Map<String, Object> customBody = new HashMap<>();

        HttpEntity<Object> httpEntityFromRequest = RequestUtility.getHttpEntityFromRequest(request, customHeader, customBody);

        Assert.assertEquals(value, httpEntityFromRequest.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
    }

    @Test
    public void whenBodyHasParameterReturnsInBodyAttributes() {
        Map<String, String> customHeader = new HashMap<>();
        Map<String, Object> customBody = new HashMap<>();
        String key = "key";
        String value = "value";
        customBody.put(key, value);

        HttpEntity<Object> httpEntityFromRequest = RequestUtility.getHttpEntityFromRequest(request, customHeader, customBody);

        Assert.assertEquals(value, ((List) ((Map) httpEntityFromRequest.getBody()).get(key)).get(0));
    }


    @Test
    public void whenBodyHasOnlyEmptyKeyReturnsBodyWithAllObjectAttributes() {
        Map<String, String> customHeader = new HashMap<>();
        Map<String, Object> customBody = new HashMap<>();
        String key = "";
        Map values = new HashMap();
        values.put("key1","value1");
        values.put("key2","value2");
        customBody.put(key, values);

        HttpEntity<Object> httpEntityFromRequest = RequestUtility.getHttpEntityFromRequest(request, customHeader, customBody);

        Assert.assertEquals(values.keySet().size(), ((Map) httpEntityFromRequest.getBody()).keySet().size());
    }

    @Test
    public void whenParametersAreNullReturnsEmptyAttributes() {
        HttpEntity<Object> httpEntityFromRequest = RequestUtility.getHttpEntityFromRequest(null, null, null);

        Assert.assertEquals(0, ((Map) httpEntityFromRequest.getBody()).keySet().size());
        Assert.assertEquals(0, httpEntityFromRequest.getHeaders().size());
    }


    @Test
    public void whenCreateJsonHeaderReturnsProperDefaultValues() {
        Map<String, String> jsonHeader = RequestUtility.createJsonHeader();

        Assert.assertTrue(jsonHeader.containsKey(HttpHeaders.CONTENT_TYPE));
        Assert.assertTrue(jsonHeader.containsKey(HttpHeaders.ACCEPT));
    }
}