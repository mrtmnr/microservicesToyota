package com.toyota.authservice.Service;
import com.toyota.authservice.DTOs.DeleteUserDTO;
import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String deleteUserById(int id) {

        if (userRepository.existsById(id)){

            userRepository.deleteById(id);

            return "User has been deleted successfully.";
        }

        return "Error: user not found with given id.";

    }


    @Override
    public List<UserResponse> findAll(Optional<String> keyword) {

        List<User>userList;

        if (keyword.isPresent()){
            userList= userRepository.filter(keyword.get());
        }
        else{
            userList=userRepository.findAll();

        }

        return userList.stream().map(this::mapToUserResponse).toList();
    }

    private UserResponse mapToUserResponse(User user) {

       return UserResponse.builder()
                .id(user.getId())
                .roles(user.getRole().stream().map(role -> role.getName().getRole()).toList())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

    }


    @Override
    public List<UserResponse> sortUserByField(String field) {
        List<User>users= userRepository.findAll(Sort.by(Sort.Direction.ASC,field));
        return users.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    public List<UserResponse> getPaginatedUsers(int offset, int pageSize) {
        List<User>users=userRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
        return users.stream().map(this::mapToUserResponse).toList();
    }


    //TEST IT
    public List<UserResponse>getPaginatedAndSortedUsers(int offset,int pageSize,String field){

        List<User>users=userRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();
        return users.stream().map(this::mapToUserResponse).toList();

    }

    @Override
    public void deleteByUsername(DeleteUserDTO deleteUserDTO) {
        String username=deleteUserDTO.getUsername();
        Optional<User>user=userRepository.findByUsername(username);
        if (user.isPresent()){
            userRepository.deleteById(user.get().getId());
        }
        else {
            throw new RuntimeException("user not found with given username! : "+username);
        }
    }



}
