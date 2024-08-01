package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.DTOs.UserResponse;
import com.toyota.managementservice.Feign.AuthProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService{

    private AuthProxy authProxy;

    @Autowired
    public ManagementServiceImpl(AuthProxy authProxy) {
        this.authProxy = authProxy;
    }

    /**
     * Adds a new user with the provided signup request.
     *
     * @param signupRequest the request containing user details for registration
     * @return a ResponseEntity indicating the result of the user registration
     */
    @Override
    public ResponseEntity<?> addUser(SignupRequest signupRequest) {
        log.info("Adding new user: {}", signupRequest.getUsername());

        String response= authProxy.registerUser(signupRequest, Optional.empty());
        log.debug("Received response from auth service: {}", response);
        //log.info("response: {}",response);

        if (response.equals("User has been registered successfully!"))
        {
            log.info("User has been registered successfully: {}", signupRequest.getUsername());

            return ResponseEntity.ok(response);

        }

        log.error("Failed to register user: {}", signupRequest.getUsername());

        return ResponseEntity
                    .badRequest()
                    .body(response);

    }

    /**
     * Updates an existing user with the provided signup request and user ID.
     *
     * @param signupRequest the request containing updated user details
     * @param id the ID of the user to be updated
     * @return a ResponseEntity indicating the result of the user update
     */
    @Override
    public ResponseEntity<?> updateUser(SignupRequest signupRequest, int id){

        String response= authProxy.registerUser(signupRequest,Optional.of(id));
        log.debug("Received response from auth service: {}", response);

        if (response.equals("User has been updated successfully!"))
        {

            return ResponseEntity.ok(response);

        }

        log.error("Failed to update user: {}", signupRequest.getUsername());
        return ResponseEntity
                .badRequest()
                .body(response);

    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to be deleted
     * @return a ResponseEntity indicating the result of the user deletion
     */
    @Override
    public ResponseEntity<?> deleteUserById(int id) {

        String response= authProxy.deleteUserById(id);
        log.debug("Received response from auth service: {}", response);

        if (response.startsWith("Error")){
            log.error("Error deleting user by id - {}", response);
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity
                .ok(response);

    }

    /**
     * Retrieves all users, optionally filtered by a keyword.
     *
     * @param keyword an optional keyword for filtering users
     * @return a list of UserResponse objects representing the users
     */
    @Override
    public List<UserResponse> getAllUsers(Optional<String> keyword) {
        return authProxy.getUsers(keyword);
    }

    /**
     * Retrieves all users sorted by the specified field.
     *
     * @param field the field by which to sort the users
     * @return a list of UserResponse objects representing the sorted users
     */
    @Override
    public List<UserResponse> sortUserByField(String field) {
        log.info("Sorting users by field: {}", field);
        return authProxy.sortUserByField(field);
    }

    /**
     * Retrieves paginated users with the specified offset and page size.
     *
     * @param offset the offset from which to start retrieving users
     * @param pageSize the number of users to retrieve
     * @return a list of UserResponse objects representing the paginated users
     */

    @Override
    public List<UserResponse> getPaginatedUsers(int offset, int pageSize) {
        log.info("Fetching paginated users. Offset: {}, PageSize: {}", offset, pageSize);
        return authProxy.getPaginatedUsers(offset,pageSize);
    }

    /**
     * Retrieves paginated and sorted users with the specified offset, page size, and sorting field.
     *
     * @param offset the offset from which to start retrieving users
     * @param pageSize the number of users to retrieve
     * @param field the field by which to sort the users
     * @return a list of UserResponse objects representing the paginated and sorted users
     */
    @Override
    public List<UserResponse> getPaginatedAndSortedUsers(int offset, int pageSize, String field) {
        log.info("Fetching paginated and sorted users. Offset: {}, PageSize: {}, Field: {}", offset, pageSize, field);
        return authProxy.getPaginatedAndSortedUsers(offset,pageSize,field);
    }


}



