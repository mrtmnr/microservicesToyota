package com.toyota.authservice.Service;


import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    String deleteUserById(int id);

    List<UserResponse> getAllUsers(Optional<String> keyword);

    List<UserResponse> sortUserByField(String field);

    List<UserResponse> getPaginatedUsers(int offset, int pageSize);

    List<UserResponse> getPaginatedAndSortedUsers(int offset, int pageSize, String field);

    UserResponse mapToUserResponse(User user);

}
