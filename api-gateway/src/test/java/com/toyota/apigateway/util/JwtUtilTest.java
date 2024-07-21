package com.toyota.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    private JwtUtil jwtUtil;
    private String jwtSecret = "testSecret";
    private int jwtExpirationMs = 3600000;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        // Directly setting the properties for testing
        jwtUtil.setJwtSecret(jwtSecret);
        jwtUtil.setJwtExpirationMs(jwtExpirationMs);

    }


    @Test
    void testGetClaims() {
        List<String>roles=new ArrayList<>(Collections.singleton("ADMIN"));
        String token = generateTestToken("testuser", roles);
        Claims claims = jwtUtil.getClaims(token);
        assertEquals("ADMIN", claims.get("roles", List.class).get(0));
    }


    @Test
    void testGetRoles() {
        List<String>roles=new ArrayList<>(Collections.singleton("ADMIN"));
        roles.add("MANAGER");
        String token = generateTestToken("testuser", roles);
        List<String> rolesFromToken = jwtUtil.getRoles(token);
        assertEquals(2, rolesFromToken.size());
        assertEquals("ADMIN", rolesFromToken.get(0));
        assertEquals("MANAGER", rolesFromToken.get(1));
    }


    @Test
    public void testGetUsernameFromJwtToken() {
        List<String>roles=new ArrayList<>(Collections.singleton("ADMIN"));

        String token = generateTestToken("mert",roles);

        String username = jwtUtil.getUsernameFromJwtToken(token);

        assertEquals("mert", username);
    }


    @Test
    public void shouldValidateJwtToken() {
        List<String>roles=new ArrayList<>(Collections.singleton("ADMIN"));

        String validToken = generateTestToken("mert",roles);
        assertTrue(jwtUtil.validateJwtToken(validToken));

    }

    @Test
    public void shouldNotValidateJwtTokenWithSignatureException() {
        List<String>roles=new ArrayList<>(Collections.singleton("ADMIN"));

        String validToken = generateTestToken("mert",roles);
        String invalidToken = validToken.substring(0, validToken.length() - 1);
        assertFalse(jwtUtil.validateJwtToken(invalidToken));
    }

    @Test
    public void shouldNotValidateWithEmptyStringToken() {
        String emptyStringToken = " ";
        assertFalse(jwtUtil.validateJwtToken(emptyStringToken));

    }
    @Test
    public void testExpiredJwtToken() {
        String expiredToken = generateExpiredToken();
        assertFalse(jwtUtil.validateJwtToken(expiredToken));
    }

    @Test
    public void testTokenWithWrongKey() {
        String incorrectKey="wrong_key";
        String token = Jwts.builder().setSubject("mert").signWith(SignatureAlgorithm.HS512, incorrectKey).compact();
        assertFalse(jwtUtil.validateJwtToken(token));
    }

    private String generateExpiredToken() {
        return Jwts.builder()
                .setSubject("mert")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired token
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String generateTestToken(String username, List<String>roles) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("roles",roles)
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }



}