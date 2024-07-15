package com.toyota.authservice.Controller;

import com.toyota.authservice.DTOs.LoginRequest;
import com.toyota.authservice.DTOs.SignupRequest;
import com.toyota.authservice.DTOs.UserResponse;

import com.toyota.authservice.Service.AuthService;
import com.toyota.authservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {


    private UserService userService;

    private AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){


        return authService.authenticateUser(loginRequest);

    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody SignupRequest signUpRequest,@RequestParam Optional<Integer>id) {

        return authService.registerUser(signUpRequest,id);

    }

    @DeleteMapping("/deleteUserById")
    public String deleteUserById(@RequestParam("id") int id){

       return userService.deleteUserById(id);

    }

    @GetMapping("/listUsers")
    public List<UserResponse> getUsers(@RequestParam Optional<String> keyword){

        return userService.getAllUsers(keyword);

    }

    @GetMapping("/sortUserByField")
    public List<UserResponse> sortUserByField(@RequestParam String field) {
        return userService.sortUserByField(field);
    }

    @GetMapping("/paginateUsers")
    public List<UserResponse> getPaginatedUsers(@RequestParam int offset, @RequestParam int pageSize) {
        return userService.getPaginatedUsers(offset,pageSize);
    }


    @GetMapping("/paginateAndSortUsers")
    public List<UserResponse>getPaginatedAndSortedUsers(@RequestParam int offset,@RequestParam int pageSize,@RequestParam String field){

        return userService.getPaginatedAndSortedUsers(offset,pageSize,field);

    }



}

