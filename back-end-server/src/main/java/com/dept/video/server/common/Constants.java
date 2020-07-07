package com.dept.video.server.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Utility to manage reusable constants.
 */
@Getter
@NoArgsConstructor
@Component
public class Constants implements Serializable {

    public static final String AUTHORIZATION_TOKEN = "authorization token";
    public static final String STRING = "string";
    public static final String HEADER = "header";
    public static final String AUTHORIZATION = "Authorization";
    private static final long serialVersionUID = 1L;

    @Value("${paginator.page}")
    private int paginatorPage;

    @Value("${paginator.size}")
    private int paginatorSize;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.prefix}")
    private String jwtTokePrefix;

    @Value("${jwt.header.string}")
    private String jwtHeaderString;

    @Value("${jwt.expiration.time}")
    private long jwtExpirationTime;

}