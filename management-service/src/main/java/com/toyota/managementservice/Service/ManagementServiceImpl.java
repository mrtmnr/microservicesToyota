package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.Feign.AuthProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        String response= authProxy.registerUser(signupRequest);

        log.info("response: {}",response);

        if (response.equals("User registered successfully!")){


            return ResponseEntity.ok(response);


        }


        return ResponseEntity
                    .badRequest()
                    .body(response);


    }
}
