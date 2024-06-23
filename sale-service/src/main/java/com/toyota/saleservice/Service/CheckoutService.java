package com.toyota.saleservice.Service;

import com.toyota.saleservice.Entity.Checkout;

public interface CheckoutService {


    void save(Checkout checkout);

    void deleteById(int id);

    Checkout findById(int id);
}
