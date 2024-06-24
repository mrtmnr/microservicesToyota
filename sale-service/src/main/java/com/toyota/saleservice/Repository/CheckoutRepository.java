package com.toyota.saleservice.Repository;

import com.toyota.saleservice.Entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckoutRepository extends JpaRepository<Checkout,Integer> {


    @Query("SELECT c FROM Checkout c WHERE c.id = (SELECT MAX(c2.id) FROM Checkout c2)")
    Checkout findTopByOrderByIdDesc();

}
