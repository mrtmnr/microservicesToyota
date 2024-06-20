package com.toyota.apigateway.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${sau.app.jwtSecret}")
    private String jwtSecret;



    public void validateJwtToken(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        } catch (MalformedJwtException e) {
           throw new RuntimeException("Error: Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e){
            throw new RuntimeException("Error: JWT token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Error: JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: JWT claims string is empty: " + e.getMessage());
        }
    }
}