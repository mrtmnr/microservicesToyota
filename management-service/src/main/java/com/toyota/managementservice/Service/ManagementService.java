package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface ManagementService {
    ResponseEntity<?> addUser(SignupRequest signupRequest);
}
