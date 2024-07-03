package com.toyota.managementservice.Controller;


import com.toyota.managementservice.DTOs.SignupRequest;
import com.toyota.managementservice.Service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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












}
