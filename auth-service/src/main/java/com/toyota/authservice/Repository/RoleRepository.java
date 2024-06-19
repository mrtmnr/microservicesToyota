package com.toyota.authservice.Repository;


import com.toyota.authservice.Entity.Role;
import com.toyota.authservice.Enum.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(EnumRole name);
}
