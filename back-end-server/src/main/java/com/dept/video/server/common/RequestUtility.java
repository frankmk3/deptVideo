package com.dept.video.server.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to generate request data
 */
@Slf4j
public final class RequestUtility {

    private RequestUtility() {
        //prevent initialization
    }

    /**
     * Conform the proper HttpEntity object.
     * Populates the headers values and the body object
     *
     * @param request
     * @param customHeader
     * @param bodyParameter
     * @return
     */
    public static HttpEntity<Object> getHttpEntityFromRequest(
            HttpServletRequest request,
            Map<String, String> customHeader,
            Map<String, Object> bodyParameter
    ) {
        HttpHeaders headers = new HttpHeaders();
        if (request != null) {
            Collections.list(request.getHeaderNames()).forEach(key -> headers.set(key, request.getHeader(key)));
        }
        Object body;
        if (customHeader != null) {
            customHeader.keySet().forEach(key -> headers.set(key, customHeader.get(key)));
        }
        if (bodyParameter == null) {
            body = new LinkedMultiValueMap<>();
        } else if (bodyParameter.size() == 1 && bodyParameter.containsKey("")) {
            body = bodyParameter.get("");
        } else {
            body = new LinkedMultiValueMap<>();
            bodyParameter.keySet().forEach(key -> ((LinkedMultiValueMap) body).add(key, bodyParameter.get(key)));
        }
        return new HttpEntity<>(body, headers);
    }


    public static Map<String, String> createJsonHeader() {
        Map<String, String> customHeader = new HashMap<>();
        customHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        customHeader.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return customHeader;
    }

}
