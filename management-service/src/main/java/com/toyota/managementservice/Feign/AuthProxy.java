package com.toyota.managementservice.Feign;
import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.DTOs.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "auth-service")
@Component
public interface AuthProxy {


    @PostMapping("/auth/signup")
    public String registerUser(@RequestBody SignupRequest signUpRequest, @RequestParam Optional<Integer>id);

    @DeleteMapping("/auth/deleteUser")
    public String deleteUserById(@RequestParam("id") int id);


    @GetMapping("/auth/listUsers")
    public List<UserResponse> getUsers(@RequestParam Optional<String> keyword);

    @GetMapping("/auth/sortUserByField")
    public List<UserResponse> sortUserByField(@RequestParam String field);
    @GetMapping("/auth/paginateUsers")
    public List<UserResponse> getPaginatedUsers(@RequestParam int offset, @RequestParam int pageSize);


    @GetMapping("/auth/paginateAndSortUsers")
    public List<UserResponse>getPaginatedAndSortedUsers(@RequestParam int offset,@RequestParam int pageSize,@RequestParam String field);


}
