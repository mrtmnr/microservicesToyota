package com.toyota.apigateway.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtil {
    @Value("${sau.app.jwtSecret}")
    private String jwtSecret;


    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }


    public List<String> getRoles(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", List.class);
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }



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