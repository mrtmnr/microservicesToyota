package com.toyota.saleservice.Service;

import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Repository.CheckoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    CheckoutService underTest;

    @Mock
    CheckoutRepository checkoutRepository;

    @BeforeEach
    void setUp() {
        underTest=new CheckoutServiceImpl(checkoutRepository);
    }

    @Test
    void shouldReturnCheckoutById() {
        //given
        int id=1;
        Checkout checkout=new Checkout();
        checkout.setId(id);

        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));
        //when
        Checkout checkout1=underTest.findById(id);
        //then
        assertThat(checkout1).isEqualTo(checkout);

    }


    @Test
    void shouldThrowExceptionWithInvalidId() {
        //given
        int id=9;
        //then
        assertThatThrownBy(()->
                underTest.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("there is bo checkout with given id: "+id);

    }

    @Test
    void shouldReturnLastCheckout() {
        //given
        //when
        underTest.getLastCheckout();
        //then
        verify(checkoutRepository).findLastCheckout();
    }

    @Test
    void shouldSaveCheckout() {
        //given
        Checkout checkout=new Checkout();
        //when
        underTest.save(checkout);
        //then
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void shouldDeleteCheckoutById() {
        //given
        int id=1;
        //when
        underTest.deleteById(id);
        //then
        verify(checkoutRepository).deleteById(id);
    }
}