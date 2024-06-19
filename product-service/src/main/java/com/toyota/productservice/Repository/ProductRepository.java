package com.toyota.productservice.Repository;

import com.toyota.productservice.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    Optional<Product> findProductByTitle(String title);

    @Query("SELECT p FROM Product p WHERE CONCAT(p.id, ' ', p.title, ' ', p.price, ' ', p.stock, ' ', p.campaign.title, ' ', p.category.title) LIKE %?1%")
    List<Product> filter(String keyword);

}
