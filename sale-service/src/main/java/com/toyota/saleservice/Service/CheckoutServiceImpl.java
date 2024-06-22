package com.toyota.saleservice.Service;

import com.example.toyotamono.Entity.Checkout;
import com.example.toyotamono.Entity.User;
import com.example.toyotamono.Repository.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private CheckoutRepository checkoutRepository;

    @Autowired
    public CheckoutServiceImpl(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    @Override
    public boolean existsByUser(User user) {
        return checkoutRepository.existsByUser(user);
    }

    @Override
    public Checkout findByUser(User user) {
        return checkoutRepository.findByUser(user);
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
