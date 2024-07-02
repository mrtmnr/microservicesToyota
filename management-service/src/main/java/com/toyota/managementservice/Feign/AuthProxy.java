package com.toyota.managementservice.Feign;
import com.toyota.managementservice.DTOs.SignupRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
@Component
public interface AuthProxy {


    @PostMapping("/auth/signup")
    public String registerUser(@RequestBody SignupRequest signUpRequest);




}
