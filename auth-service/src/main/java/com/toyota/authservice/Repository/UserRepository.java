package com.toyota.authservice.Repository;


import com.toyota.authservice.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.username, ' ', u.email) LIKE %?1%")
    List<User> filter(String keyword);



}
