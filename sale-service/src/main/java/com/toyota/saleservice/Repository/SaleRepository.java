package com.toyota.saleservice.Repository;

import com.toyota.saleservice.Entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SaleRepository extends JpaRepository<Sale,Integer> {

    @Query("SELECT s FROM Sale s WHERE CONCAT(s.id, ' ', s.date, ' ', s.totalPrice, ' ', s.payment, ' ', s.username) LIKE %?1%")
    List<Sale> filter(String keyword);
}
