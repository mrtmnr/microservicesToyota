package com.toyota.authservice.Security.jwt;


import com.toyota.authservice.Security.Services.UserDetailsImpl;
import io.jsonwebtoken.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {


    private JwtUtils jwtUtils;
    private String jwtSecret = "testSecret";
    private int jwtExpirationMs = 3600000;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JwtUtils();
        // Directly setting the properties for testing
        jwtUtils.setJwtSecret(jwtSecret);
        jwtUtils.setJwtExpirationMs(jwtExpirationMs);

    }

    @Test
    public void testGenerateJwtToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);

        when(userDetails.getUsername()).thenReturn("mert");

        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Parse the token to check its contents
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        assertThat(claims.getSubject()).isEqualTo("mert");
    }

    @Test
    public void testGetUsernameFromJwtToken() {
        String token = generateTestToken("mert");

        String username = jwtUtils.getUsernameFromJwtToken(token);

        assertEquals("mert", username);
    }


    @Test
    public void shouldValidateJwtToken() {
        String validToken = generateTestToken("mert");
        assertTrue(jwtUtils.validateJwtToken(validToken));

    }

    @Test
    public void shouldNotValidateJwtTokenWithSignatureException() {
        String validToken = generateTestToken("mert");
        String invalidToken = validToken.substring(0, validToken.length() - 1);
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
       // verify(log).error("Error: Invalid JWT token: " + anyString());

    }

    @Test
    public void shouldNotValidateWithIllegalArgumentException() {
        String validToken = generateTestToken("mert");
        String invalidToken = " ";
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
        // verify(log).error("Error: Invalid JWT token: " + anyString());

    }
    @Test
    public void testExpiredJwtToken() {
        String expiredToken = generateExpiredToken();
        assertFalse(jwtUtils.validateJwtToken(expiredToken));
      //  verify(log).error("Error: JWT token expired: " + anyString());
    }

    @Test
    public void testTokenWithWrongKey() {
        String incorrectKey="wrong_key";
        String token = Jwts.builder().setSubject("mert").signWith(SignatureAlgorithm.HS512, incorrectKey).compact();
        assertFalse(jwtUtils.validateJwtToken(token));
    }


    private String generateExpiredToken() {
        return Jwts.builder()
                .setSubject("mert")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired token
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    private String generateTestToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }



}