package com.toyota.saleservice.Service;

import com.example.toyotamono.Entity.Checkout;
import com.example.toyotamono.Entity.User;

public interface CheckoutService {

    boolean existsByUser(User user);

    Checkout findByUser(User user);

    void save(Checkout checkout);

    void deleteById(int id);
}
