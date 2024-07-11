package com.toyota.saleservice.Service;


import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Repository.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private CheckoutRepository checkoutRepository;

    @Autowired
    public CheckoutServiceImpl(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }


    @Override
    public Checkout findById(int id) {
        Optional<Checkout>optionalCheckout= checkoutRepository.findById(id);

        if (optionalCheckout.isPresent()){
            return optionalCheckout.get();
        }

        throw new RuntimeException("there is bo checkout with given id: "+id);

    }

    @Override
    public Checkout getLastCheckout() {
        return checkoutRepository.findLastCheckout();
    }

    @Override
    public void save(Checkout checkout) {
        checkoutRepository.save(checkout);
    }

    @Override
    public void deleteById(int id) {
        checkoutRepository.deleteById(id);
    }
}
