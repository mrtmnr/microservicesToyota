package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ManagementService {
    ResponseEntity<?> addUser(SignupRequest signupRequest);


    ResponseEntity<?> updateUser(SignupRequest signupRequest, int id);
}
