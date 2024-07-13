package com.toyota.saleservice.Repository;

import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Entity.Entry;
import com.toyota.saleservice.Entity.Sale;
import com.toyota.saleservice.Enum.EnumPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @BeforeEach
    void setUp() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JULY, 5, 12, 0);

        Sale sale1=new Sale("mert",200,new Date(), EnumPayment.CARD);
        Sale sale2=new Sale("merve",111,new Date(calendar.getTimeInMillis()), EnumPayment.CASH);

        Checkout checkout1=new Checkout(192);
        Checkout checkout2=new Checkout(103);


        sale1.setCheckout(checkout1);

        sale2.setCheckout(checkout2);

        saleRepository.save(sale1);
        saleRepository.save(sale2);
    }


    @Test
    void testFilterSaleByPrice(){

        //given
        String price= String.valueOf(192);

        //when
        List<Sale> saleList=saleRepository.filter(price);


        //then
        assertThat(saleList).hasSize(1);
        String stringPrice= String.valueOf(saleList.get(0).getCheckout().getTotalPrice());
        assert (stringPrice.contains(price));

    }


    @Test
    void testFilterSaleByDate(){

        //given
        String day="13";

        //when
        List<Sale> saleList=saleRepository.filter(day);


        //then
        assertThat(saleList).hasSize(1);
        String stringDate=saleList.get(0).getDate().toString();
        System.out.println("stringDate: "+stringDate);
        assert (stringDate.contains(day));

    }

    @Test
    void testFilterSaleByPayment(){

        //given
        String payment= String.valueOf(EnumPayment.CARD);

        //when
        List<Sale> saleList=saleRepository.filter(payment);


        //then
        assertThat(saleList).hasSize(1);
        String stringPayment= String.valueOf(saleList.get(0).getPayment());
        assert (stringPayment.contains(payment));

    }

    @Test
    void testFilterSaleByUsername(){

        //given
        String username="mert";

        //when
        List<Sale>saleList=saleRepository.filter(username);


        //then
        assertThat(saleList).hasSize(1);
        assertThat(saleList.get(0).getUsername()).isEqualTo(username);

    }



    @Test
    void findAllOrderByCheckoutTotalPriceAsc(){


        //given

        Sale sale1=new Sale("mert",100,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(100);
        Entry entry1=new Entry(2,33,true);
        Entry entry2=new Entry(3,78,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry>entries= Arrays.asList(entry1,entry2);
        checkout.setEntries(entries);
        sale1.setCheckout(checkout);


        saleRepository.save(sale1);

        //when
        List<Sale>sales=saleRepository.findAllOrderByCheckoutTotalPriceAsc();

        //then
        assertThat(sales.get(0).getCheckout().getTotalPrice()).isEqualTo(100f);
        assertThat(sales.get(1).getCheckout().getTotalPrice()).isEqualTo(103f);
        assertThat(sales.get(2).getCheckout().getTotalPrice()).isEqualTo(192.0f);








    }
}