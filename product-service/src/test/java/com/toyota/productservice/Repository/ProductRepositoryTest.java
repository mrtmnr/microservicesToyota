package com.toyota.productservice.Repository;

import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;


import java.util.List;
import java.util.Optional;



@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @BeforeEach
    void setUp() {
        String title= "phone";

        Product product=new Product(title,5,4);

        Category category = new Category();
        category.setTitle("Electronics");


        product.setCategory(category);

        productRepository.save(product);
    }



    @Test
    void shouldReturnProductWhenFindByTitle() {

        //given
        String title="phone";

        //when
        Optional<Product> optionalProduct=productRepository.findProductByTitle(title);

        //then
        assertThat(optionalProduct).isPresent();
        assertThat(optionalProduct.get().getTitle()).isEqualTo(title);

    }

    @Test
    void testFilterProductByTitle(){

        //given
        String title="phone";

        //when
        List<Product> productList=productRepository.filter(title);

        //then
        assertThat(productList).hasSize(1);
        assert (productList.get(0).getTitle()).contains(title);

    }

    @Test
    void testFilterProductByPrice(){

        //given
        String price= String.valueOf(5);

        //when
        List<Product> productList=productRepository.filter(price);


        //then
        assertThat(productList).hasSize(1);
        String stringPrice= String.valueOf(productList.get(0).getPrice());
        assert (stringPrice.contains(price));



    }

    @Test
    void testFilterProductByStock(){

        //given
        String stock= String.valueOf(4);

        //when
        List<Product> productList=productRepository.filter(stock);


        //then
        assertThat(productList).hasSize(1);
        String stringStock= String.valueOf(productList.get(0).getStock());
        assert (stringStock.contains(stock));



    }


    @Test
    void testFilterProductByCategoryTitle(){

        //given
        String categoryTitle="Electronics";

        //when
        List<Product> productList=productRepository.filter(categoryTitle);


        //then
        assertThat(productList).hasSize(1);

        assert (productList.get(0).getCategory().getTitle().contains(categoryTitle));


    }



}