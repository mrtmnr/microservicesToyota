package com.toyota.authservice.Security.jwt;


import com.toyota.authservice.Security.Services.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {
    @Value("${sau.app.jwtSecret}")
    private String jwtSecret;

    @Value("${sau.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();


        //add roles to token as a string
        List<String>roles=userPrincipal.getAuthorities().stream().map(Object::toString).toList();


        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    public String getUsernameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Error: s Invalid JWT token: " + e.getMessage());
        }  catch (ExpiredJwtException e){
            log.error("Error: JWT token expired: " + e.getMessage());
        }
        catch (IllegalArgumentException e){
            log.error("Error: JWT String argument is null or empty." + e.getMessage());
        }
        return false;
    }


    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setJwtExpirationMs(int jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }
}
