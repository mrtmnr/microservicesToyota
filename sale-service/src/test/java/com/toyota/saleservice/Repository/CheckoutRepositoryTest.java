package com.toyota.saleservice.Repository;


import com.toyota.saleservice.Entity.Checkout;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class CheckoutRepositoryTest {

    @Autowired
    CheckoutRepository checkoutRepository;


    @Test
    public void testFindLastCheckout() {

        //given
        Checkout checkout1 = new Checkout();
        checkout1.setTotalPrice(100);
        checkoutRepository.save(checkout1);

        Checkout checkout2 = new Checkout();
        checkout2.setTotalPrice(200);
        checkoutRepository.save(checkout2);

        Checkout checkout3 = new Checkout();
        checkout3.setTotalPrice(300);
        checkoutRepository.save(checkout3);

        //when
        Checkout lastCheckout = checkoutRepository.findLastCheckout();

        //then
        assertThat(lastCheckout).isNotNull();
        assertThat(lastCheckout.getId()).isEqualTo(checkout3.getId());
    }


}