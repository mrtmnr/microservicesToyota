package com.toyota.authservice.Service;


import com.toyota.authservice.DTOs.DeleteUserDTO;
import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    String deleteUserById(int id);

    List<UserResponse> findAll(Optional<String> keyword);

    List<UserResponse> sortUserByField(String field);

    List<UserResponse> getPaginatedUsers(int offset, int pageSize);

    List<UserResponse> getPaginatedAndSortedUsers(int offset, int pageSize, String field);

    void deleteByUsername(DeleteUserDTO deleteUserDTO);

}
