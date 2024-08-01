package com.toyota.apigateway.filter;

import com.toyota.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private RouteValidator validator;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("path of the request received -> {}",exchange.getRequest().getPath());
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return handleUnauthorized(exchange, "Missing authorization header !");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);

                }

                boolean isTokenValid = jwtUtil.validateJwtToken(authHeader);
                if (!isTokenValid) {
                    return handleUnauthorized(exchange, "Invalid JWT Token");
                }

                //log.info("requiredRolesForServices: {}" , auth.requiredRolesForServices);

                log.info("Token validated successfully!");

                List<String> userRoles = jwtUtil.getRoles(authHeader);

                String path = exchange.getRequest().getPath().toString();


                // let the product path pass without checking authorization
                if (!(path.startsWith("/product/"))){

                    Optional<Map.Entry<String, List<String>>> requiredRolesEntry = requiredRolesForServices().entrySet().stream()
                            .filter(entry -> path.startsWith(entry.getKey()))
                            .findFirst();

                    if (requiredRolesEntry.isPresent()) {
                        List<String> requiredRoles = requiredRolesEntry.get().getValue();
                        boolean hasRequiredRole = userRoles.stream().anyMatch(requiredRoles::contains);

                        if (!hasRequiredRole) {
                            log.error("User does not have the required role for path: {}", path);
                            throw new RuntimeException("User does not have the required role");
                        }

                        log.info("User is authorized for path: {}", path);
                    } else {
                        log.warn("No roles configured for path: {}", path);
                    }

                    String username = jwtUtil.getUsernameFromJwtToken(authHeader);
                    exchange.getRequest().mutate().header("username", username).build();

                }

            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        log.error("Unauthorized access: {}", message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\": \"" + message + "\"}").getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }


    private Map<String, List<String>> requiredRolesForServices(){

        Map<String, List<String>> map = new HashMap<>();

        //map.put("/product/",List.of("MANAGER","ADMIN","CASHIER"));
        map.put("/sale/",List.of("CASHIER"));
        map.put("/report/",List.of("MANAGER"));
        map.put("/manage/",List.of("ADMIN"));

       return map;
    }


    public static class Config {

    }
}