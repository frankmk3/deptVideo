package com.dept.video.server.security;

import com.dept.video.server.common.Constants;
import com.dept.video.server.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String VERSION = "version";
    private final Constants constants;
    private final String secretKey;
    @Setter
    @Autowired
    private UserService userService;

    @Autowired
    public JwtTokenProvider(Constants constants) {
        this.constants = constants;
        secretKey = Base64.getEncoder().encodeToString(constants.getJwtSecret().getBytes());
    }

    public String createToken(String username, String version) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + constants.getJwtExpirationTime());
        Map<String, Object> header = new HashMap<>();
        header.put(VERSION, version);
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setHeader(header)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsernameAndVersion(getUsername(token), getUserVersion(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserVersion(String token) {
        return String.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getHeader().get(VERSION));
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(constants.getJwtHeaderString());
        if (bearerToken != null && bearerToken.startsWith(constants.getJwtTokePrefix())) {
            return bearerToken.substring(constants.getJwtTokePrefix().length(), bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (MalformedJwtException e) {
            return false;
        }
        return true;
    }
}