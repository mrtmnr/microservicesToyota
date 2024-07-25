package com.toyota.productservice.Repository;


import com.toyota.productservice.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {

    Optional<Category> findByTitle(String title);
  

}
