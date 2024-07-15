package com.toyota.authservice.Repository;

import com.toyota.authservice.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {

        User user=new User("mert","mertmunar@gmail.com","123");
        userRepository.save(user);
    }

    @Test
    void findByUsernameShouldReturnUserWithValidUsername() {

        //given
        //when
        Optional<User>optionalUser=userRepository.findByUsername("mert");

        //then
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getUsername()).isEqualTo("mert");
    }

    @Test
    void findByUsernameWithInvalidUsernameShouldNotReturnUser() {

        //given
        //when
        Optional<User>optionalUser=userRepository.findByUsername("merve");

        //then
        assertThat(optionalUser).isNotPresent();

    }




    @Test
    void existsByUsernameShouldReturnTrueWithValidUsername(){

        //when
        Boolean userExists=userRepository.existsByUsername("mert");

        //then
        assertThat(userExists).isTrue();


    }

    @Test
    void existsByUsernameShouldReturnFalseWithInvalidUsername(){

        //when
        Boolean userExists=userRepository.existsByUsername("merve");

        //then
        assertThat(userExists).isFalse();


    }

    @Test
    void existsByEmailShouldReturnTrueWithValidEmail(){

        //when
        Boolean userExists=userRepository.existsByEmail("mertmunar@gmail.com");

        //then
        assertThat(userExists).isTrue();


    }

    @Test
    void existsByEmailShouldReturnFalseWithInvalidEmail(){

        //when
        Boolean userExists=userRepository.existsByEmail("mervemunar@gmail.com");

        //then
        assertThat(userExists).isFalse();


    }




    @Test
    void shouldFilterUserByUsername(){

        //given
        String keyword="mert";


        //when
        List<User>userList=userRepository.filter(keyword);

        //then
        assertThat(userList).hasSize(1);
        assertThat(userList.get(0).getUsername().contains(keyword)).isTrue();

    }

    @Test
    void shouldNotFilterUserByInvalidUsername(){

        //given
        String keyword="merve";


        //when
        List<User>userList=userRepository.filter(keyword);

        //then
        assertThat(userList).hasSize(0);

    }



    @Test
    void shouldFilterUserByEmail(){

        //given
        String keyword="munar";


        //when
        List<User>userList=userRepository.filter(keyword);

        //then
        assertThat(userList).hasSize(1);
        assertThat(userList.get(0).getEmail().contains(keyword)).isTrue();

    }


    @Test
    void shouldNotFilterUserByInvalidEmail(){

        //given
        String keyword="yılmaz";

        //when
        List<User>userList=userRepository.filter(keyword);

        //then
        assertThat(userList).hasSize(0);

    }







}