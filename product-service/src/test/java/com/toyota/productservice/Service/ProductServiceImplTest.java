package com.toyota.productservice.Service;

import com.toyota.productservice.DTOs.ProductDTO;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Entity.Product;
import com.toyota.productservice.Repository.CampaignRepository;
import com.toyota.productservice.Repository.CategoryRepository;
import com.toyota.productservice.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {


    ProductService underTest;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CampaignRepository campaignRepository;

    @Captor
    ArgumentCaptor<Product>productArgumentCaptor;


    @BeforeEach
    void setUp(){

        underTest=new ProductServiceImpl(productRepository,categoryRepository,campaignRepository);

    }

    @Test
    void shouldReturnAllProducts() {
        //given
        //when
        underTest.findAll();
        //then
        verify(productRepository).findAll();


    }


    @Test
    void testMapToProductResponseWithCampaign() {
        //given
        Product product1 = new Product("apple", 10, 20);
        Category category = new Category("fruit");
        Campaign campaign = new Campaign("%3 sale", 3, true, false);
        product1.setCategory(category);
        product1.setCampaign(campaign);
        product1.setId(1);

        //when
        ProductResponse response = underTest.mapToProductResponse(product1);

        //then
        assertEquals(1, response.getId());
        assertEquals(20, response.getStock());
        assertEquals(10, response.getPrice());
        assertEquals("apple", response.getTitle());
        assertEquals("%3 sale", response.getCampaignName());
        assertEquals("fruit", response.getCategoryName());

    }
    @Test
    void testMapToProductResponseWithoutCampaign() {
        //given
        Product product1 = new Product("apple", 10, 20);
        Category category = new Category("fruit");
        product1.setCategory(category);
        product1.setId(1);

        //when
        ProductResponse response = underTest.mapToProductResponse(product1);

        //then
        assertEquals(1, response.getId());
        assertEquals(20, response.getStock());
        assertEquals(10, response.getPrice());
        assertEquals("apple", response.getTitle());
        assertNull (response.getCampaignName());
        assertEquals("fruit", response.getCategoryName());

    }

    @Test
    void mapToProductDTOWithCampaign() {
        Product product1 = new Product("apple", 10, 20);
        Category category = new Category("fruit");
        Campaign campaign = new Campaign("%3 sale", 3, true, false);
        campaign.setId(2);
        product1.setCategory(category);
        product1.setCampaign(campaign);
        product1.setId(1);

        //when
        ProductDTO productDTO = underTest.mapToProductDTO(product1);

        //then
        assertEquals(product1.getId(), productDTO.getId());
        assertEquals(product1.getStock(), productDTO.getStock());
        assertEquals(product1.getPrice(), productDTO.getPrice());
        assertEquals(product1.getTitle(), productDTO.getTitle());
        assertEquals(category.getTitle(), productDTO.getCategoryName());

        assertThat(productDTO.getCampaignDTO().getId()).isEqualTo(campaign.getId());
        assertThat(productDTO.getCampaignDTO().getTitle()).isEqualTo(campaign.getTitle());
        assertThat(productDTO.getCampaignDTO().getDiscountPercentage()).isEqualTo(campaign.getDiscountPercentage());
        assertThat(productDTO.getCampaignDTO().isOneFreeActive()).isEqualTo(campaign.isOneFreeActive());
        assertThat(productDTO.getCampaignDTO().isPercentageActive()).isEqualTo(campaign.isPercentageActive());


    }

    @Test
    void mapToProductDTOWithoutCampaign() {

        Product product1 = new Product("apple", 10, 20);
        Category category = new Category("fruit");

        product1.setCategory(category);

        product1.setId(1);

        //when
        ProductDTO productDTO = underTest.mapToProductDTO(product1);

        //then
        assertEquals(product1.getId(), productDTO.getId());
        assertEquals(product1.getStock(), productDTO.getStock());
        assertEquals(product1.getPrice(), productDTO.getPrice());
        assertEquals(product1.getTitle(), productDTO.getTitle());
        assertEquals(category.getTitle(), productDTO.getCategoryName());
        assertNull(productDTO.getCampaignDTO());

    }



    @Test
    void shouldReturnProductDTOCampaignById() {
        // given
        int id = 1;
        Product product = new Product("apple", 10, 20);
        Category category=new Category("fruit");
        product.setCategory(category);
        product.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        ProductDTO response = underTest.getProductById(id);

        // then
        assertThat(response.getId()).isEqualTo(product.getId());
        assertThat(response.getTitle()).isEqualTo(product.getTitle());
        assertThat(response.getStock()).isEqualTo(product.getStock());
        assertThat(response.getCategoryName()).isEqualTo(product.getCategory().getTitle());

    }


    @Test
    void shouldThrowExceptionWhenProductNotFoundById() {
        // given
        int id = 1;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when


        // then
        assertThatThrownBy(()->
                underTest.getProductById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("product not found with given id: "+id);

    }


    @Test
    @Disabled
    void getProductListByIds() {
    }

    @Test
    @Disabled
    void updateStock() {




    }

    @Test
    void shouldReturnProductDTOWithCampaignByTitle() {
        //given
        String title="banana";
        Product product=new Product(title,10,20);
        Category category = new Category("fruit");
        Campaign campaign = new Campaign("%3 sale", 3, true, false);
        product.setCampaign(campaign);
        product.setCategory(category);

        when(productRepository.findProductByTitle(title)).thenReturn(Optional.of(product));

        //when
        ProductDTO response = underTest.getProductByTitle(title);

        //then
        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getTitle(), response.getTitle());
        assertEquals(product.getStock(), response.getStock());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getCategory().getTitle(),response.getCategoryName());
        assertEquals(product.getCampaign().getId(),response.getCampaignDTO().getId());


    }



    @Test
    void shouldReturnProductDTOWithoutCampaignByTitle() {
        //given
        String title="banana";
        Product product=new Product(title,10,20);
        Category category = new Category("fruit");
        product.setCategory(category);

        when(productRepository.findProductByTitle(title)).thenReturn(Optional.of(product));

        //when
        ProductDTO response = underTest.getProductByTitle(title);

        //then
        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getTitle(), response.getTitle());
        assertEquals(product.getStock(), response.getStock());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getCategory().getTitle(),response.getCategoryName());
        assertNull(response.getCampaignDTO());


    }

    @Test
    void shouldNotReturnProductDTOByTitleAndThrowExceptionWhenTitleNotFound() {

        //given
        String title="table";

        when(productRepository.findProductByTitle(title)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.getProductByTitle(title))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("product was not found by title: "+title);



    }


    @Test
    void shouldSaveProduct() {

        //given
        String title="tomato";
        Product product=new Product(title,50,200);
        Category category = new Category("vegetable");
        product.setCategory(category);


        //when
        underTest.save(product);


        //then
        verify(productRepository).save(product);

    }

    @Test
    void shouldDeleteProductIfProductExistWithGivenIdWhenDeleteById(){
        //given
        int id=1;
        when(productRepository.existsById(1)).thenReturn(true);
        //when
        underTest.deleteById(1);
        //then
        verify(productRepository).deleteById(id);

    }

    @Test
    void shouldThrowRuntimeExceptionIfProductNotExistWithGivenIdWhenDeleteById(){
        //given
        int id=1;
        when(productRepository.existsById(1)).thenReturn(false);
        //when
        //then
        assertThatThrownBy(()->
                underTest.deleteById(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There is no product with given id: "+id);


        verify(productRepository,never()).deleteById(any());

    }






    @Test
    @Disabled
    void sortProductByField() {
    }

    @Test
    @Disabled
    void getPaginatedProducts() {
    }

    @Test
    @Disabled
    void getPaginatedAndSortedProducts() {
    }

    @Test
    @Disabled
    void findAllResponses() {
    }

    @Test
    @Disabled
    void addProduct() {
    }


}