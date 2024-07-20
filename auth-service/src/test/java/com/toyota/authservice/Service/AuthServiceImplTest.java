package com.toyota.authservice.Service;

import com.toyota.authservice.DTOs.JwtResponse;
import com.toyota.authservice.DTOs.LoginRequest;
import com.toyota.authservice.DTOs.SignupRequest;
import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Enum.EnumRole;
import com.toyota.authservice.Repository.RoleRepository;
import com.toyota.authservice.Repository.UserRepository;
import com.toyota.authservice.Security.Services.UserDetailsImpl;
import com.toyota.authservice.Security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    private AuthServiceImpl authService;


    @BeforeEach
    void setUp() {
        authService=new AuthServiceImpl(jwtUtils,authenticationManager,userRepository,roleRepository,passwordEncoder);
    }

    @Test
    void authenticateUserWhenUsernameAndPasswordAreValid() {
        //given
        String username = "testuser";
        String password = "testpass";
        LoginRequest loginRequest = new LoginRequest(username,password);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(1, username, "testemail@test.com", password, Collections.emptyList()));
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        //when
        ResponseEntity<?> response = authService.authenticateUser(loginRequest);

        //then
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getToken()).isEqualTo("jwt-token");
        assertThat(jwtResponse.getUsername()).isEqualTo(username);
    }

    @Test
    void shouldNotRegisterUserWithUsernameAlreadyTaken() {
        SignupRequest signupRequest = new SignupRequest("existingUser", "password", "email@test.com", Set.of("ADMIN"));

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        String response = authService.registerUser(signupRequest, Optional.empty());

        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: Username is already taken!");
    }

    @Test
    void shouldNotRegisterUserWithEmailAlreadyInUse() {
        SignupRequest signupRequest = new SignupRequest("existingUser", "password", "existingEmail@test.com", Set.of("ADMIN"));

        when(userRepository.existsByEmail("existingEmail@test.com")).thenReturn(true);

        String response = authService.registerUser(signupRequest, Optional.empty());

        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: Email is already in use!");
    }


    @Test
    void shouldNotRegisterUserWithEmptyRole() {
        SignupRequest signupRequest = new SignupRequest("user", "password", "existingEmail@test.com", Set.of());

        String response = authService.registerUser(signupRequest, Optional.empty());

        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: Role is not selected.");
    }


    @Test
    void shouldNotRegisterUserWithInvalidRole() {
        SignupRequest signupRequest = new SignupRequest("user", "password", "existingEmail@test.com", Set.of("invalid-role"));

        String response = authService.registerUser(signupRequest, Optional.empty());


        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: invalid Role.");
    }


    @Test
    void shouldRegisterUser() {
        //given
        String username = "user";
        String email = "email@test.com";
        String password = "password";
        SignupRequest signupRequest = new SignupRequest(username,password ,email, Set.of("ADMIN","CASHIER","MANAGER"));

        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("email@test.com")).thenReturn(false);

        EnumRole enumAdmin = EnumRole.ADMIN;
        EnumRole enumCashier = EnumRole.CASHIER;
        EnumRole enumManager = EnumRole.MANAGER;

        Role adminRole = new Role();
        adminRole.setEnumName(enumAdmin);

        Role cashierRole = new Role();
        cashierRole.setEnumName(enumCashier);


        Role managerRole = new Role();
        managerRole.setEnumName(enumManager);


        when(roleRepository.findByEnumName(enumAdmin)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByEnumName(enumCashier)).thenReturn(Optional.of(cashierRole));
        when(roleRepository.findByEnumName(enumManager)).thenReturn(Optional.of(managerRole));


        //when
        String response = authService.registerUser(signupRequest, Optional.empty());

        assertThat(response).isEqualTo("User has been registered successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(capturedUser.getRole()).contains(adminRole);
        assertThat(capturedUser.getRole()).contains(cashierRole);
        assertThat(capturedUser.getRole()).contains(managerRole);
    }


    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String username = "testuser";
        SignupRequest signupRequest = new SignupRequest(username, "testpass","testemail@test.com",  Set.of("MANAGER"));

        when(userRepository.existsByUsername(username)).thenReturn(true);

        String response = authService.registerUser(signupRequest, Optional.empty());


        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: Username is already taken!");
    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() {
        String email = "testemail@test.com";
        SignupRequest signupRequest = new SignupRequest("testuser","testpass",  email, Set.of("MANAGER"));

        when(userRepository.existsByEmail(email)).thenReturn(true);

        String response = authService.registerUser(signupRequest, Optional.empty());

        verify(userRepository,never()).save(any());
        assertThat(response).isEqualTo("Error: Email is already in use!");
    }

    @Test
    void shouldUpdateUsernameWithValidIdAndUsername(){
        String username = "existingUser";
        String email = "existingEmail@test.com";
        String password = "existingPassword";

        int userId = 1;

        User existingUser = new User(username, email, password);

        EnumRole enumAdmin = EnumRole.ADMIN;

        Role adminRole = new Role();
        adminRole.setEnumName(enumAdmin);

        existingUser.setId(userId);
        existingUser.setRole(Set.of(adminRole));

        SignupRequest signupRequest = new SignupRequest("newUsername", null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByEnumName(enumAdmin)).thenReturn(Optional.of(adminRole));

        String response = authService.registerUser(signupRequest, Optional.of(userId));

        assertThat(response).isEqualTo("User has been updated successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo("newUsername");
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo(password);

    }

    @Test
    void shouldUpdatePasswordWithValidId(){
        String username = "existingUser";
        String email = "existingEmail@test.com";
        String password = "existingPassword";

        int userId = 1;

        User existingUser = new User(username, email, password);

        EnumRole enumAdmin = EnumRole.ADMIN;

        Role adminRole = new Role();
        adminRole.setEnumName(enumAdmin);

        existingUser.setId(userId);
        existingUser.setRole(Set.of(adminRole));

        SignupRequest signupRequest = new SignupRequest(null, "newPassword", null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded-newPassword");
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByEnumName(enumAdmin)).thenReturn(Optional.of(adminRole));

        String response = authService.registerUser(signupRequest, Optional.of(userId));

        assertThat(response).isEqualTo("User has been updated successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo("encoded-newPassword");

    }



    @Test
    void shouldUpdateEmailWithValidIdAndEmail(){

        String username = "existingUser";
        String email = "existingEmail@test.com";
        String password = "existingPassword";

        int userId = 1;

        User existingUser = new User(username, email, password);

        EnumRole enumAdmin = EnumRole.ADMIN;

        Role adminRole = new Role();
        adminRole.setEnumName(enumAdmin);

        existingUser.setId(userId);
        existingUser.setRole(Set.of(adminRole));

        SignupRequest signupRequest = new SignupRequest(null, null, "newEmail@test.com", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail("newEmail@test.com")).thenReturn(false);
        when(roleRepository.findByEnumName(enumAdmin)).thenReturn(Optional.of(adminRole));

        String response = authService.registerUser(signupRequest, Optional.of(userId));

        assertThat(response).isEqualTo("User has been updated successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo("newEmail@test.com");
        assertThat(capturedUser.getPassword()).isEqualTo(password);

    }



    @Test
    void shouldUpdateRoleWithValidIdAndRole(){

        String username = "existingUser";
        String email = "existingEmail@test.com";
        String password = "existingPassword";

        int userId = 1;

        User existingUser = new User(username, email, password);

        EnumRole enumAdmin = EnumRole.ADMIN;
        EnumRole enumCashier=EnumRole.CASHIER;

        Role existingAdminRole = new Role();
        existingAdminRole.setEnumName(enumAdmin);

        Role newCashierRole = new Role();
        newCashierRole.setEnumName(enumCashier);

        existingUser.setId(userId);
        existingUser.setRole(Set.of(existingAdminRole));

        SignupRequest signupRequest = new SignupRequest(null, null, null, Set.of("CASHIER"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByEnumName(enumCashier)).thenReturn(Optional.of(newCashierRole));

        String response = authService.registerUser(signupRequest, Optional.of(userId));

        assertThat(response).isEqualTo("User has been updated successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEmail()).isEqualTo(email);
        assertThat(capturedUser.getPassword()).isEqualTo(password);
        assertThat(capturedUser.getRole()).contains(newCashierRole);
        assertThat(capturedUser.getRole()).doesNotContain(existingAdminRole);


    }


    @Test
    void shouldUpdateAll(){

        String username = "existingUser";
        String email = "existingEmail@test.com";
        String password = "existingPassword";

        int userId = 1;

        User existingUser = new User(username, email, password);

        EnumRole enumAdmin = EnumRole.ADMIN;
        EnumRole enumCashier=EnumRole.CASHIER;

        Role existingAdminRole = new Role();
        existingAdminRole.setEnumName(enumAdmin);

        Role newCashierRole = new Role();
        newCashierRole.setEnumName(enumCashier);

        existingUser.setId(userId);
        existingUser.setRole(Set.of(existingAdminRole));

        SignupRequest signupRequest = new SignupRequest("newUsername", "newPassword", "newEmail", Set.of("CASHIER"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded-newPassword");
        when(userRepository.existsByEmail("newEmail")).thenReturn(false);
        when(roleRepository.findByEnumName(enumCashier)).thenReturn(Optional.of(newCashierRole));

        String response = authService.registerUser(signupRequest, Optional.of(userId));

        assertThat(response).isEqualTo("User has been updated successfully!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo("newUsername");
        assertThat(capturedUser.getEmail()).isEqualTo("newEmail");
        assertThat(capturedUser.getPassword()).isEqualTo("encoded-newPassword");
        assertThat(capturedUser.getRole()).contains(newCashierRole);
        assertThat(capturedUser.getRole()).doesNotContain(existingAdminRole);


    }

    @Test
    void shouldNotUpdateUserWithInvalidUserId() {

        int userId = 1;

        SignupRequest signupRequest = new SignupRequest("newUsername", "newPassword", "newEmail", Set.of("CASHIER"));


        assertThatThrownBy(()->
                authService.registerUser(signupRequest,Optional.of(userId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There is no user with given user id !");

        verify(userRepository,never()).save(any());

    }






}