package com.dept.video.server.restclient;

import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.common.RequestUtility;
import com.dept.video.server.common.TemplateUtility;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Remote service.
 */
@Service
public abstract class RemoteRestService {

    protected MessagesUtility messagesUtility;

    protected RestTemplate restTemplate;

    protected ObjectMapper objectMapper;

    protected TemplateUtility templateUtility;

    @Autowired
    public RemoteRestService(
            MessagesUtility messagesUtility,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            TemplateUtility templateUtility
    ) {
        this.messagesUtility = messagesUtility;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.templateUtility = templateUtility;
    }

    /**
     * Execute the request and process the response.
     */
    protected Map exchange(
            String url,
            HttpMethod httpMethod,
            HttpStatus positiveResponse,
            String errorMessageKey,
            Map<String, Object> bodyParameters
    ) throws EntityNotFoundException, QueryException, TooManyRequestException {
        Map<String, String> customHeader = RequestUtility.createJsonHeader();
        HttpEntity<Object> entity = RequestUtility.getHttpEntityFromRequest(null, customHeader, bodyParameters);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, httpMethod, entity, Map.class);
            if (response.getStatusCodeValue() == positiveResponse.value()) {
                return response.getBody();
            } else if (response.getStatusCodeValue() == HttpStatus.NOT_FOUND.value()) {
                throw new EntityNotFoundException(messagesUtility.getMessage("not.found"));
            } else {
                throw new QueryException(messagesUtility.getMessage(errorMessageKey));
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new EntityNotFoundException(messagesUtility.getMessage("not.found"), e);
            }
            if (e.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestException(messagesUtility.getMessage("not.found"), e);
            }
            throw e;
        }
    }

}
