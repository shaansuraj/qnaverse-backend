package com.qnaverse.QnAverse.utils;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Utility class for handling JWT token creation, validation, and parsing.
 */
@Component
public class JwtUtil {

    // Example base64-encoded key. Use application.properties or env variables in real projects.
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");
    private final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generates a JWT token for a given username and role.
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) //main part of sigining process, for some cases you can use email as
                .claim("role", role)  // Add role as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the role from a JWT token.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));  // Extract role
    }

    /**
     * Extracts claims from a JWT token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates a JWT token.
     */
    public boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
