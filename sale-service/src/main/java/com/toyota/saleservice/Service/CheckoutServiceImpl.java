package com.toyota.saleservice.Service;


import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Repository.CheckoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutService{

    private CheckoutRepository checkoutRepository;

    @Autowired
    public CheckoutServiceImpl(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }


    /**
     * Retrieves a checkout by its ID.
     *
     * @param id the ID of the checkout
     * @return the Checkout entity
     * @throws RuntimeException if the checkout is not found.
     */
    @Override
    public Checkout findById(int id) {
        Optional<Checkout>optionalCheckout= checkoutRepository.findById(id);

        if (optionalCheckout.isPresent()){
            return optionalCheckout.get();
        }

        log.error("there is no checkout with given id");
        throw new RuntimeException("there is no checkout with given id: "+id);

    }

    /**
     * Retrieves the last checkout from the repository.
     *
     * @return the last Checkout entity
     */
    @Override
    public Checkout getLastCheckout() {
        return checkoutRepository.findLastCheckout();
    }

    /**
     * Saves the given checkout to the repository.
     *
     * @param checkout the Checkout entity to be saved
     */
    @Override
    public void save(Checkout checkout) {
        checkoutRepository.save(checkout);
    }

    /**
     * Deletes a checkout by its ID.
     *
     * @param id the ID of the checkout to be deleted
     */
    @Override
    public void deleteById(int id) {
        checkoutRepository.deleteById(id);
    }
}
