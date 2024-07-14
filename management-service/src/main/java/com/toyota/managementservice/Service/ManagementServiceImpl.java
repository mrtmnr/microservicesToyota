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

    @Override
    public ResponseEntity<?> addUser(SignupRequest signupRequest) {

        String response= authProxy.registerUser(signupRequest, Optional.empty());

        //log.info("response: {}",response);

        if (response.equals("User has been registered successfully!"))
        {


            return ResponseEntity.ok(response);


        }


        return ResponseEntity
                    .badRequest()
                    .body(response);


    }

    @Override
    public ResponseEntity<?> updateUser(SignupRequest signupRequest, int id){

        String response= authProxy.registerUser(signupRequest,Optional.of(id));


        if (response.equals("User has been updated successfully!"))
        {

            return ResponseEntity.ok(response);

        }

        return ResponseEntity
                .badRequest()
                .body(response);

    }

    @Override
    public ResponseEntity<?> deleteUserById(int id) {

        String response= authProxy.deleteUserById(id);

        if (response.startsWith("Error")){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity
                .ok(response);

    }

    @Override
    public List<UserResponse> getAllUsers(Optional<String> keyword) {
        return authProxy.getUsers(keyword);
    }

    @Override
    public List<UserResponse> sortUserByField(String field) {
        return authProxy.sortUserByField(field);
    }

    @Override
    public List<UserResponse> getPaginatedUsers(int offset, int pageSize) {
        return authProxy.getPaginatedUsers(offset,pageSize);
    }

    @Override
    public List<UserResponse> getPaginatedAndSortedUsers(int offset, int pageSize, String field) {
        return authProxy.getPaginatedAndSortedUsers(offset,pageSize,field);
    }


}



