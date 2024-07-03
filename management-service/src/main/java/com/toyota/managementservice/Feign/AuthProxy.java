package com.toyota.managementservice.Feign;
import com.toyota.managementservice.DTOs.SignupRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "auth-service")
@Component
public interface AuthProxy {


    @PostMapping("/auth/signup")
    public String registerUser(@RequestBody SignupRequest signUpRequest, @RequestParam Optional<Integer>id);

    @DeleteMapping("/auth/deleteUser")
    public String deleteUserById(@RequestParam("id") int id);








}
