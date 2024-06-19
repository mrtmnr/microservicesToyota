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

    String deleteById(int id);

    List<UserResponse>findAll(Optional<String> keyword);

    List<User> sortUserByField(String field);

    List<User> getPaginatedUsers(int offset, int pageSize);

    List<User>getPaginatedAndSortedUsers(int offset,int pageSize,String field);

    void deleteByUsername(DeleteUserDTO deleteUserDTO);
}
