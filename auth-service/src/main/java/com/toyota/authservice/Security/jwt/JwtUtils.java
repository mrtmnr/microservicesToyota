package com.toyota.authservice.Security.jwt;


import com.toyota.authservice.Security.Services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${sau.app.jwtSecret}")
    private String jwtSecret;

    @Value("${sau.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        //add roles to token
        List<String>roles=userPrincipal.getAuthorities().stream().map(r->r.toString()).toList();

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
        } catch (MalformedJwtException e) {
            System.out.println("Error: Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e){
            System.out.println("Error: JWT token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Error: JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
