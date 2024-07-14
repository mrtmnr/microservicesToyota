package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.DTOs.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ManagementService {
    ResponseEntity<?> addUser(SignupRequest signupRequest);

    ResponseEntity<?> updateUser(SignupRequest signupRequest, int id);

    ResponseEntity<?> deleteUserById(int id);

    List<UserResponse> getAllUsers(Optional<String> keyword);

    List<UserResponse> sortUserByField(String field);

    List<UserResponse> getPaginatedUsers(int offset, int pageSize);

    List<UserResponse> getPaginatedAndSortedUsers(int offset, int pageSize, String field);
}
