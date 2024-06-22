package com.toyota.saleservice.Repository;

import com.toyota.saleservice.Entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRepository extends JpaRepository<Checkout,Integer> {

   // boolean existsByUser(User user);

   // Checkout findByUser(User user);

}
