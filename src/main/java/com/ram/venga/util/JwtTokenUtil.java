package com.ram.venga.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final HttpServletRequest request;
    private final String jwtSecretKey = "c3VwcG9ydCBraW5kIG1vcmUgY29tcGxpY2F0ZWQgc2VjcmV0IGtleSAxMjM0NTY3ODkw";

    public JwtTokenUtil(HttpServletRequest request) {
        this.request = request;
    }


    public String generateToken(String userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 5 * 60 * 1000);
        Map<String, Object> claims = Map.of("id", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            boolean expired = claims.getExpiration().before(new Date());
            if (expired) throw new Exception("Token is expired");
            String id = claims.get("id").toString();
            if (id == null || id.equals("")) throw new Exception("Invalid token");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractTokenFromHeader() {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public String getApiKeyFromHeader() {
        String apiKeyHeader = request.getHeader("X-API-Key");
        if (StringUtils.hasText(apiKeyHeader)) {
            return apiKeyHeader;
        } else {
            return null;
        }
    }

}
