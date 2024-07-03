package com.toyota.authservice.DTOs;

import lombok.Data;

import java.util.Optional;
import java.util.Set;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> role;
}
