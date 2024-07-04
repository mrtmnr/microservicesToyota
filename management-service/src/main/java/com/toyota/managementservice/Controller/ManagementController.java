package com.toyota.managementservice.Controller;


import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.DTOs.UserResponse;
import com.toyota.managementservice.Service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/manage")
public class ManagementController {

    private ManagementService managementService;

    @Autowired
    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody SignupRequest signupRequest){

        return managementService.addUser(signupRequest);

    }
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody SignupRequest signupRequest,@PathVariable int userId){

        return managementService.updateUser(signupRequest,userId);

    }

    @DeleteMapping("/deleteUserById/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable int id){

        return managementService.deleteUserById(id);

    }

    @GetMapping("/listUsers")
    public List<UserResponse>users(@RequestParam Optional<String> keyword){

        return managementService.getAllUsers(keyword);


    }


    @GetMapping("/sortUserByField/{field}")
    public List<UserResponse> sortUserByField(@PathVariable String field) {
        return managementService.sortUserByField(field);
    }

    @GetMapping("/paginateUsers")
    public List<UserResponse> getPaginatedUsers(@RequestParam int offset, @RequestParam int pageSize) {
        return managementService.getPaginatedUsers(offset,pageSize);
    }


    @GetMapping("/paginateAndSortUsers")
    public List<UserResponse>getPaginatedAndSortedUsers(@RequestParam int offset,@RequestParam int pageSize,@RequestParam String field){

        return managementService.getPaginatedAndSortedUsers(offset,pageSize,field);

    }











}
