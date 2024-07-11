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
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @BeforeEach
    void setUp() {

        Sale sale=new Sale("mert",200,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(192);
        Entry entry1=new Entry(3,123,true);
        Entry entry2=new Entry(1,78,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry>entries= Arrays.asList(entry1,entry2);
        checkout.setEntries(entries);
        sale.setCheckout(checkout);

        saleRepository.save(sale);
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
        String year="2024";

        //when
        List<Sale> saleList=saleRepository.filter(year);


        //then
        assertThat(saleList).hasSize(1);
        String stringDate=saleList.get(0).getDate().toString();
        System.out.println("stringDate: "+stringDate);
        assert (stringDate.contains(year));

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


        Sale sale2=new Sale("mert",55,new Date(), EnumPayment.CARD);
        Checkout checkout2=new Checkout(50);
        Entry entry3=new Entry(3,25,true);
        Entry entry4=new Entry(1,29,false);
        entry3.setProductId(1);
        entry4.setProductId(2);
        List<Entry>entries2=Arrays.asList(entry3,entry4);
        checkout.setEntries(entries2);
        sale2.setCheckout(checkout2);

        saleRepository.save(sale1);
        saleRepository.save(sale2);

        //when
        List<Sale>sales=saleRepository.findAllOrderByCheckoutTotalPriceAsc();

        //then
        assertThat(sales.get(0).getCheckout().getTotalPrice()).isEqualTo(50.0f);
        assertThat(sales.get(1).getCheckout().getTotalPrice()).isEqualTo(100.0f);
        assertThat(sales.get(2).getCheckout().getTotalPrice()).isEqualTo(192.0f);








    }
}