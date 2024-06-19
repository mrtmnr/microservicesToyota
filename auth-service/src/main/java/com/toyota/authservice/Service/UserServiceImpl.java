package com.toyota.authservice.Service;
import com.toyota.authservice.DTOs.DeleteUserDTO;
import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public String deleteById(int id) {
        userRepository.deleteById(id);
        return "user with id:  "+id+" is deleted successfully.";

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
    public List<User> sortUserByField(String field) {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC,field));
    }

    @Override
    public List<User> getPaginatedUsers(int offset, int pageSize) {
        return userRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
    }


    //TEST ET
    public List<User>getPaginatedAndSortedUsers(int offset,int pageSize,String field){

        return userRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();

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
