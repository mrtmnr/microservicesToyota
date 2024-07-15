package com.toyota.authservice.Service;

import com.toyota.authservice.DTOs.UserResponse;
import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Entity.User;
import com.toyota.authservice.Enum.EnumRole;
import com.toyota.authservice.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    UserService underTest;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {

        underTest=new UserServiceImpl(userRepository);

    }

    @Test
    void shouldDeleteUserById() {
        //given
        int id=1;

        when(userRepository.existsById(id)).thenReturn(true);

        //when
        String message=underTest.deleteUserById(id);


        //then
        verify(userRepository).deleteById(id);


        assertThat(message).isEqualTo("User has been deleted successfully.");

    }

    @Test
    void ShouldMapUserToUserResponse() {

        //given
        Role admin = new Role();
        admin.setEnumName(EnumRole.ADMIN);

        Role cashier = new Role();
        cashier.setEnumName(EnumRole.CASHIER);

        User user = new User("mert","mrtmnr@gmail.com,","123");

        user.setRole(Set.of(admin,cashier));

        //when
        UserResponse userResponse = underTest.mapToUserResponse(user);

        //then
        assertThat(userResponse.getUsername()).isEqualTo(user.getUsername());
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
        List<String>stringUserRoles=user.getRole().stream().map(r->r.getEnumName().toString()).toList();
        assertThat(userResponse.getRoles()).isEqualTo(stringUserRoles);

    }

    @Test
    void shouldReturnAllUsersAsUserResponseWithoutKeyword() {

        //given
        Role admin = new Role();
        admin.setEnumName(EnumRole.ADMIN);

        Role cashier = new Role();
        cashier.setEnumName(EnumRole.CASHIER);

        User user1 = new User("mert","mrtmnr@gmail.com,","123");
        User user2 = new User("merve","mrvmnr@gmail.com,","12345");
        user1.setId(1);
        user2.setId(2);

        user1.setRole(Set.of(admin,cashier));
        user2.setRole(Set.of(admin));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        //when
        List<UserResponse> result = underTest.getAllUsers(Optional.empty());

        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    public void shouldReturnAllUsersAsUserResponseWithKeyword() {
        // given
        String keyword="mert";

        Role admin = new Role();
        admin.setEnumName(EnumRole.ADMIN);

        Role cashier = new Role();
        cashier.setEnumName(EnumRole.CASHIER);

        User user = new User("mert","mrtmnr@gmail.com,","123");
        user.setId(1);


        when(userRepository.filter(keyword)).thenReturn(List.of(user));

        // Act
        List<UserResponse> result = underTest.getAllUsers(Optional.of(keyword));

        // Assert
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(user.getId());
    }



}