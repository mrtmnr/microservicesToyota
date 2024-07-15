package com.toyota.authservice.Repository;

import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Enum.EnumRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        EnumRole admin = EnumRole.ADMIN;

        Role role=new Role();
        role.setEnumName(admin);

        roleRepository.save(role);
    }

    @Test
    void shouldReturnRoleByName() {

        //given



        //when
        Optional<Role>optionalRole=roleRepository.findByEnumName(EnumRole.ADMIN);


        //then
        assertThat(optionalRole).isPresent();
        assertThat(optionalRole.get().getEnumName()).isEqualTo(EnumRole.ADMIN);


    }


    @Test
    void shouldNotReturnRoleByNameWhenNameIsInvalid() {

        //given
        //when
        Optional<Role>optionalRole=roleRepository.findByEnumName(EnumRole.CASHIER);


        //then
        assertThat(optionalRole).isNotPresent();



    }





}