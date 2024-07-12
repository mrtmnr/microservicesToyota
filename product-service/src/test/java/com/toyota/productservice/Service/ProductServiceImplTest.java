package com.toyota.productservice.Service;

import com.toyota.productservice.DTOs.ProductDTO;
import com.toyota.productservice.DTOs.ProductRequest;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    void shouldReturnProductListByIds() {
        // given
        int productId1 = 1;
        int productId2 = 2;
        List<Integer> productIds = Arrays.asList(productId1, productId2);

        Category category=new Category("fruit");

        Product product1 = new Product("apple",10,20);
        product1.setId(productId1);
        product1.setCategory(category);

        Product product2 = new Product("banana",30,90);
        product2.setId(productId2);
        product2.setCategory(category);

        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));

        // when
        List<ProductDTO> productDTOList = underTest.getProductListByIds(productIds);

        // then
        assertNotNull(productDTOList);
        assertEquals(2, productDTOList.size());
        assertEquals("apple", productDTOList.get(0).getTitle());
        assertEquals("banana", productDTOList.get(1).getTitle());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // given
        int productId1 = 1;
        int productId2 = 2;
        List<Integer> productIds = Arrays.asList(productId1, productId2);

        Category category=new Category("fruit");

        Product product1 = new Product("apple",10,20);
        product1.setId(productId1);
        product1.setCategory(category);


        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.empty());

        //when
        // then
        assertThatThrownBy(()->
                underTest.getProductListByIds(productIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("no such product with given id: "+productId2);

    }





    @Test
    void updateStockOfProductList() {
        //given
        int updatedStock=100;

        ProductResponse productResponse1 = new ProductResponse();
        ProductResponse productResponse2 = new ProductResponse();
        productResponse1.setStock(updatedStock);
        productResponse2.setStock(updatedStock);
        productResponse1.setId(1);
        productResponse2.setId(2);

        Product product1 = new Product("apple", 10, 20);
        Product product2 = new Product("banana", 5, 10);

        Category category=new Category("fruit");
        product1.setCategory(category);
        product2.setCategory(category);

        List<ProductResponse> products = Arrays.asList(productResponse1, productResponse2);

        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));

        //when
        underTest.updateStock(products);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository, times(2)).save(productArgumentCaptor.capture());

        List<Product> capturedProducts = productArgumentCaptor.getAllValues();

        assertThat(capturedProducts).hasSize(2);
        assertThat(capturedProducts.get(0).getStock()).isEqualTo(updatedStock);
        assertThat(capturedProducts.get(1).getStock()).isEqualTo(updatedStock);


    }

    @Test
    void dontUpdateStockWhenProductIsNotFound() {
        //given
        int updatedStock=100;

        ProductResponse productResponse1 = new ProductResponse();
        productResponse1.setStock(updatedStock);
        productResponse1.setId(1);

        Product product1 = new Product("apple", 10, 20);

        Category category=new Category("fruit");
        product1.setCategory(category);


        List<ProductResponse> products = new ArrayList<>();
        products.add(productResponse1);

        when(productRepository.findById(1)).thenReturn(Optional.empty());


        //when

        // then
        assertThatThrownBy(()->
                underTest.updateStock(products))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");

        verify(productRepository,never()).save(product1);


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
    void shouldSortProductsByGivenField() {
        // given
        String field = "price";
        Product product1 = new Product("apple", 10, 20);
        Product product2 = new Product("banana", 5, 30);
        Category category=new Category("fruit");
        product1.setCategory(category);
        product2.setCategory(category);
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll(Sort.by(Sort.Direction.ASC, field))).thenReturn(products);

        // when
        List<Product> sortedProducts = underTest.sortProductByField(field);

        // then
        assertNotNull(sortedProducts);
        assertEquals(2, sortedProducts.size());
        assertEquals("apple", sortedProducts.get(0).getTitle());
        assertEquals("banana", sortedProducts.get(1).getTitle());
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
    void shouldReturnAllProductResponsesWhenKeywordIsNotPresent() {
        // given
        Product product1 = new Product("apple", 10, 20);
        Product product2 = new Product("banana", 5, 30);
        Category category=new Category("fruit");
        product1.setCategory(category);
        product2.setCategory(category);
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        // when
        List<ProductResponse> responses = underTest.findAllResponses(Optional.empty());

        // then
        assertEquals(2, responses.size());
        assertEquals("apple", responses.get(0).getTitle());
        assertEquals("banana", responses.get(1).getTitle());
    }


    @Test
    void shouldReturnFilteredProductResponsesWhenKeywordIsPresent() {
        // given
        String keyword = "apple";
        Product product = new Product("apple", 10, 20);
        Category category=new Category("fruit");
        product.setCategory(category);
        List<Product> products=new ArrayList<>();
        products.add(product);

        when(productRepository.filter(keyword)).thenReturn(products);

        // when
        List<ProductResponse> responses = underTest.findAllResponses(Optional.of(keyword));

        // then
        assertEquals(1, responses.size());
        assertThat(responses.get(0).getTitle()).isEqualTo(product.getTitle());
    }




    @Test
    void dontAddProductWhenCategoryNameIsInvalid() {

        ProductRequest productRequest=new ProductRequest("electronics","phone",1000,566,null);


        //given
        when(categoryRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.addProduct(productRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(productRequest.getCategory()+" is invalid category !");


        verify(productRepository,never()).save(any());

    }

    @Test
    void dontAddProductWhenCampaignIdIsInvalid() {

        ProductRequest productRequest=new ProductRequest("electronics","phone",1000,566,1);
        Category category=new Category("electronics");



        //given
        when(categoryRepository.findByTitle(anyString())).thenReturn(Optional.of(category));
        when(campaignRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.addProduct(productRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(productRequest.getCampaignId()+"is invalid campaignId!");


        verify(productRepository,never()).save(any());

    }


    @Test
    void shouldAddProductWithCampaign() {

        //given
        ProductRequest productRequest=new ProductRequest("electronics","phone",1000,566,1);
        Category category=new Category("electronics");
        Campaign campaign = new Campaign("%10 sale", 10, true, false);

        when(categoryRepository.findByTitle("electronics")).thenReturn(Optional.of(category));
        when(campaignRepository.findById(productRequest.getCampaignId())).thenReturn(Optional.of(campaign));

        //when
        underTest.addProduct(productRequest);
        //then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productArgumentCaptor.capture());

        Product capturedProduct=productArgumentCaptor.getValue();

        assertThat(capturedProduct.getCampaign().getTitle()).isEqualTo(campaign.getTitle());
        assertThat(capturedProduct.getCampaign().getDiscountPercentage()).isEqualTo(campaign.getDiscountPercentage());
        assertThat(capturedProduct.getCampaign().isPercentageActive()).isEqualTo(campaign.isPercentageActive());
        assertThat(capturedProduct.getCampaign().isOneFreeActive()).isEqualTo(campaign.isOneFreeActive());

    }



}