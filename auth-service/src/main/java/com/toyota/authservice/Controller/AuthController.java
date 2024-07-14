package com.toyota.authservice.Controller;

import com.toyota.authservice.DTOs.JwtResponse;
import com.toyota.authservice.DTOs.LoginRequest;
import com.toyota.authservice.DTOs.SignupRequest;
import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Enum.EnumRole;
import com.toyota.authservice.Repository.RoleRepository;
import com.toyota.authservice.Repository.UserRepository;
import com.toyota.authservice.Security.Services.UserDetailsImpl;
import com.toyota.authservice.Security.jwt.JwtUtils;

import com.toyota.authservice.Service.AuthService;
import com.toyota.authservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    
    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
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

    @DeleteMapping("/deleteUser")
    public String deleteUserById(@RequestParam("id") int id){

        log.info("deleteUserById triggered");

        if (userRepository.existsById(id)){

            userRepository.deleteById(id);

            return "User has been deleted successfully.";
        }

        return "Error: user not found with given id.";


    }

    @GetMapping("/listUsers")
    public List<UserResponse> getUsers(@RequestParam Optional<String> keyword){

        if (keyword.isPresent()){
            log.info("search keyword: {}",keyword);
        }

        return userService.findAll(keyword);

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

