package com.toyota.apigateway.filter;

import com.toyota.apigateway.util.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //log.info("path of the request received -> {}",exchange.getRequest().getPath());
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);

                }
//
                jwtUtil.validateJwtToken(authHeader);

                //log.info("requiredRolesForServices: {}" , auth.requiredRolesForServices);


                log.info("token validated !");

                List<String> userRoles = jwtUtil.getRoles(authHeader);

                String path = exchange.getRequest().getPath().toString();


                //let the product path pass without checking authorization
                if (!(path.startsWith("/product/"))){

                    Optional<Map.Entry<String, List<String>>> requiredRolesEntry = requiredRolesForServices().entrySet().stream()
                            .filter(entry -> path.startsWith(entry.getKey()))
                            .findFirst();

                   // System.out.println(requiredRolesForServices().entrySet().stream().);

                    if (requiredRolesEntry.isPresent()) {
                        List<String> requiredRoles = requiredRolesEntry.get().getValue();
                        boolean hasRequiredRole = userRoles.stream().anyMatch(requiredRoles::contains);

                        if (!hasRequiredRole) {
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