package com.toyota.apigateway.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
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



    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Error: Invalid JWT token: " + e.getMessage());
        } catch (MalformedJwtException e){
            log.error("Error: malformed Invalid JWT token:  " + e.getMessage());
        }  catch (ExpiredJwtException e){
            log.error("Error: JWT token expired: " + e.getMessage());
        }
        return false;
    }
}