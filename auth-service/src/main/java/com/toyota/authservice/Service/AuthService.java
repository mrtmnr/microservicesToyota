package com.toyota.authservice.Service;

import com.toyota.authservice.DTOs.LoginRequest;
import com.toyota.authservice.DTOs.SignupRequest;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    String registerUser(SignupRequest signUpRequest,Optional<Integer> id);
}
