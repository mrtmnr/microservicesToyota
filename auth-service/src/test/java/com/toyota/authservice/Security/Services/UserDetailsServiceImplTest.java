package com.toyota.authservice.Security.Services;


import com.toyota.authservice.Entity.User;

import com.toyota.authservice.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    UserDetailsService underTest;

    @BeforeEach
    void setUp() {
        underTest=new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void shouldLoadUserByUsername() {

        //given
        String username="mert";

        User user = new User(username,"mrtmnr@gmail.com,","123");



        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = underTest.loadUserByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    public void shouldNotLoadUserByInvalidUsername() {
        // given
        String username = "nonExistUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username: " + username);

    }


}