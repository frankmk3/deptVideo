package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

    private String id;
    private String name;
    private String email;
    private String source;
    private String token;
    private String lang;
    private String role;

    private Map<String, Object> properties;
    private Map<String, Map<String, Object>> clients;
}
