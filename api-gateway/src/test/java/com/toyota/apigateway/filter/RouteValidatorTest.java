package com.toyota.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class RouteValidatorTest {

    private RouteValidator routeValidator;

    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
    }

    @Test
    void testIsSecuredWithOpenApiEndpoints() {
        List<String> openApiEndpoints = RouteValidator.openApiEndpoints;

        for (String endpoint : openApiEndpoints) {
            ServerHttpRequest request = MockServerHttpRequest.get(endpoint).build();
            Predicate<ServerHttpRequest> isSecured = routeValidator.isSecured;

            assertFalse(isSecured.test(request), "The endpoint " + endpoint + " should not be secured.");
        }
    }

    @Test
    void testIsSecuredWithSecuredEndpoints() {
        List<String> securedEndpoints = List.of(
                "/product/123",
                "/sale/123",
                "/report/123",
                "/manage/123"
        );

        for (String endpoint : securedEndpoints) {
            ServerHttpRequest request = MockServerHttpRequest.get(endpoint).build();
            Predicate<ServerHttpRequest> isSecured = routeValidator.isSecured;

            assertTrue(isSecured.test(request), "The endpoint " + endpoint + " should be secured.");
        }
    }

    @Test
    void testIsSecuredWithNestedOpenApiEndpoints() {
        List<String> nestedOpenApiEndpoints = List.of(
                "/auth/signin/details",
                "/auth/signup/info",
                "/eureka/status"
        );

        for (String endpoint : nestedOpenApiEndpoints) {
            ServerHttpRequest request = MockServerHttpRequest.get(endpoint).build();
            Predicate<ServerHttpRequest> isSecured = routeValidator.isSecured;

            assertFalse(isSecured.test(request), "The nested endpoint " + endpoint + " should not be secured.");
        }
    }

    @Test
    void testIsSecuredWithMixedEndpoints() {
        List<String> mixedEndpoints = List.of(
                "/auth/signin",
                "/product/123",
                "/auth/signup/info",
                "/sale/123",
                "/eureka/status",
                "/report/123",
                "/manage/123"
        );

        for (String endpoint : mixedEndpoints) {
            ServerHttpRequest request = MockServerHttpRequest.get(endpoint).build();
            Predicate<ServerHttpRequest> isSecured = routeValidator.isSecured;
            boolean expectedResult = !RouteValidator.openApiEndpoints.contains(endpoint) && !endpoint.startsWith("/auth/") && !endpoint.startsWith("/eureka");

            assertEquals(expectedResult, isSecured.test(request), "The endpoint " + endpoint + " did not have the expected secured status.");
        }
    }

    private void assertEquals(boolean expectedResult, boolean actualResult, String s) {
    }

}