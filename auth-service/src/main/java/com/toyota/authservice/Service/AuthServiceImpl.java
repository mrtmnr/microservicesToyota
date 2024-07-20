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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    private JwtUtils jwtUtils;

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;


    @Autowired
    public AuthServiceImpl(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
        );

        log.info("Sign-in successful !");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @Override
    public String registerUser(SignupRequest signUpRequest, Optional<Integer> id) {

        boolean usernameCheck=true;
        boolean emailCheck=true;


        if(id.isPresent()){

            int userId=id.get();

            Optional<User> matchedUser=userRepository.findById(userId);
            if (matchedUser.isPresent()){

                if (signUpRequest.getEmail()==null){

                    signUpRequest.setEmail(matchedUser.get().getEmail());
                    emailCheck=false;

                }
                if (signUpRequest.getUsername()==null){

                    signUpRequest.setUsername(matchedUser.get().getUsername());
                    usernameCheck=false;

                }

                if (signUpRequest.getPassword()==null){

                    signUpRequest.setPassword(matchedUser.get().getPassword());

                }
                else {
                    signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                }
                if (signUpRequest.getRole()==null){


                    signUpRequest.setRole(matchedUser.get().getRole().stream().map(r->r.getEnumName().toString()).collect(Collectors.toSet()));

                }

            }
            else {
                throw new RuntimeException("There is no user with given user id !");
            }
        }
        else {

            signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())&&usernameCheck) {
            log.error("Error: Username is already taken!");
            return "Error: Username is already taken!";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())&&emailCheck) {
            log.error("Error: Email is already in use!");
            return "Error: Email is already in use!";
        }

        log.info("user will be created shortly!");

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());
        id.ifPresent(user::setId);

        Set<String> strRoles = signUpRequest.getRole();
        log.info("roles: "+ strRoles);
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {

            log.error("Error: Role is not selected.");
            return  "Error: Role is not selected.";

        }

        //check whether roles are expected
        boolean match= strRoles.stream().allMatch(r->r.equals("CASHIER")||r.equals("ADMIN")||r.equals("MANAGER"));

        if (!match){

            log.error("Error: invalid Role.");
            return "Error: invalid Role.";
        }


        strRoles.forEach(role -> {
            switch (role) {
                case "ADMIN":
                    Role adminRole = roleRepository.findByEnumName(EnumRole.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);

                    break;
                case "CASHIER":
                    Role cashierRole = roleRepository.findByEnumName(EnumRole.CASHIER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(cashierRole);

                    break;
                default:
                    Role managerRole = roleRepository.findByEnumName(EnumRole.MANAGER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(managerRole);
            }
        });

        user.setRole(roles);
        userRepository.save(user);

        if(id.isPresent()){
            return "User has been updated successfully!";
        }

        return "User has been registered successfully!";
    }
}
