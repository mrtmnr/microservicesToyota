package com.toyota.authservice.Controller;

import com.toyota.authservice.DTOs.JwtResponse;
import com.toyota.authservice.DTOs.LoginRequest;
import com.toyota.authservice.DTOs.MessageResponse;
import com.toyota.authservice.DTOs.SignupRequest;
import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Enum.EnumRole;
import com.toyota.authservice.Repository.RoleRepository;
import com.toyota.authservice.Repository.UserRepository;
import com.toyota.authservice.Security.Services.UserDetailsImpl;
import com.toyota.authservice.Security.jwt.JwtUtils;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping("/deneme")
    public String deneme(){

        return "olduuuuu !";
    }



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam SignupRequest signUpRequest,@RequestParam Optional<Integer>id) {


        boolean usernameCheck=true;
        boolean emailCheck=true;


        if(id.isPresent()){

            int userId=id.get();

            Optional<User> updateUser=userRepository.findById(userId);
            if (updateUser.isPresent()){

                if (signUpRequest.getEmail()==null){

                    signUpRequest.setEmail(updateUser.get().getEmail());
                    emailCheck=false;

                }
                if (signUpRequest.getUsername()==null){

                    signUpRequest.setUsername(updateUser.get().getUsername());
                    usernameCheck=false;

                }
                if (signUpRequest.getPassword()==null){

                    signUpRequest.setPassword(updateUser.get().getPassword());

                }
                else {
                    signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                }
                if (signUpRequest.getRole()==null){


                    signUpRequest.setRole(updateUser.get().getRole().stream().map(r->r.getName().toString()).collect(Collectors.toSet()));

                }

            }

        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())&&usernameCheck) {
            return "Error: Username is already taken!";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())&&emailCheck) {
            return "Error: Email is already in use!";
        }


        log.info("user will be created shortly!");
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());
        id.ifPresent(user::setId);

        Set<String> strRoles = signUpRequest.getRole();
        log.info("roles: "+ strRoles);
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {

                   return  "Error: Role is not selected.";

           }

        //check whether roles are expected
        boolean match= strRoles.stream().allMatch(r->r.equals("CASHIER")||r.equals("ADMIN")||r.equals("MANAGER"));

        if (!match){

            return "Error: invalid Role.";
        }


            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(EnumRole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "CASHIER":
                        Role cashierRole = roleRepository.findByName(EnumRole.CASHIER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(cashierRole);

                        break;
                    default:
                        Role managerRole = roleRepository.findByName(EnumRole.MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);
                }
            });

        user.setRole(roles);
        userRepository.save(user);

        if(id.isPresent()){
            return "User updated successfully!";
        }

        return "User registered successfully!";
    }





}

