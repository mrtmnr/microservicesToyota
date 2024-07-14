package com.toyota.managementservice.Service;

import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.Feign.AuthProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {

    ManagementService underTest;

    @Mock
    AuthProxy authProxy;

    @BeforeEach
    void setUp() {
        underTest=new ManagementServiceImpl(authProxy);
    }

    @Test
    void shouldAddUser() {
        // Given
        SignupRequest signupRequest = new SignupRequest();


        String responseMessage = "User has been registered successfully!";
        when(authProxy.registerUser(signupRequest, Optional.empty())).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.addUser(signupRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotAddUserWhenRoleIsNotSelected() {
        // Given
        SignupRequest signupRequest = new SignupRequest();


        String responseMessage = "Error: Role is not selected.";
        when(authProxy.registerUser(signupRequest, Optional.empty())).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.addUser(signupRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotAddUserWhenRoleIsInvalid() {
        // Given
        SignupRequest signupRequest = new SignupRequest();


        String responseMessage = "Error: invalid Role.";
        when(authProxy.registerUser(signupRequest, Optional.empty())).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.addUser(signupRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotAddUserWhenUsernameIsTaken() {
        // Given
        SignupRequest signupRequest = new SignupRequest();


        String responseMessage = "Error: Username is already taken!";
        when(authProxy.registerUser(signupRequest, Optional.empty())).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.addUser(signupRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }
    @Test
    void shouldNotAddUserWhenEmailIsTaken() {
        // Given
        SignupRequest signupRequest = new SignupRequest();


        String responseMessage = "Error: Email is already in use!";
        when(authProxy.registerUser(signupRequest, Optional.empty())).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.addUser(signupRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldUpdateUser() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        int userId = 1;

        String responseMessage = "User has been updated successfully!";
        when(authProxy.registerUser(signupRequest, Optional.of(userId))).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.updateUser(signupRequest, userId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotUpdateUserWhenRoleIsNotSelected() {
        SignupRequest signupRequest = new SignupRequest();
        int userId=1;


        String responseMessage = "Error: Role is not selected.";
        when(authProxy.registerUser(signupRequest, Optional.of(userId))).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.updateUser(signupRequest,userId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotUpdateUserWhenRoleIsInvalid() {
        SignupRequest signupRequest = new SignupRequest();
        int userId=1;


        String responseMessage = "Error: invalid Role.";
        when(authProxy.registerUser(signupRequest, Optional.of(userId))).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.updateUser(signupRequest,userId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }

    @Test
    void shouldNotUpdateUserWhenUsernameIsTaken() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        int userId=1;

        String responseMessage = "Error: Username is already taken!";
        when(authProxy.registerUser(signupRequest, Optional.of(userId))).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.updateUser(signupRequest,userId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }
    @Test
    void shouldNotUpdateUserWhenEmailIsTaken() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        int userId=1;


        String responseMessage = "Error: Email is already in use!";
        when(authProxy.registerUser(signupRequest, Optional.of(userId))).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.updateUser(signupRequest,userId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }


    @Test
    void shouldDeleteUserWhenUserIdIsValid() {
        // Given
        int UserId=1;

        String responseMessage = "User has been deleted successfully.";
        when(authProxy.deleteUserById(UserId)).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.deleteUserById(UserId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }


    @Test
    void shouldNotDeleteUserWhenUserIdIsValid() {
        // Given
        int UserId=1;

        String responseMessage = "Error: user not found with given id.";
        when(authProxy.deleteUserById(UserId)).thenReturn(responseMessage);

        // When
        ResponseEntity<?> responseEntity = underTest.deleteUserById(UserId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseMessage, responseEntity.getBody());
    }





    @Test
    void deleteUserById() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void sortUserByField() {
    }

    @Test
    void getPaginatedUsers() {
    }

    @Test
    void getPaginatedAndSortedUsers() {
    }
}