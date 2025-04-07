package com.mycity.apigateway.config;

import java.security.Key;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secretKey = "mySecretKeyWhichIsAtLeast32Chars!"; // Replace with your secure key

    // Token expiry time in milliseconds (e.g., 24 hours)
    private final long tokenExpiry = 86400000; // 24 hours

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(); // Direct bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("JWT Token has expired: " + e.getMessage());
            return false;
        } catch (io.jsonwebtoken.SignatureException e) {
            System.err.println("JWT Signature is invalid: " + e.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("JWT Token is malformed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error validating JWT Token: " + e.getMessage());
            return false;
        }
    }


    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Extracted Claims: " + claims);
            return claims;
        } catch (Exception e) {
            System.err.println("Error extracting claims from JWT Token: " + e.getMessage());
            return null;
        }
    }

}