package com.toyota.productservice.Service;


import com.toyota.productservice.DTOs.CampaignDTO;
import com.toyota.productservice.DTOs.ProductRequest;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.DTOs.ProductDTO;
import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Entity.Product;
import com.toyota.productservice.Repository.CampaignRepository;
import com.toyota.productservice.Repository.CategoryRepository;
import com.toyota.productservice.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    private CampaignRepository campaignRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CampaignRepository campaignRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.campaignRepository = campaignRepository;
    }

    /**
     * Finds all products.
     *
     * @return a list of all products
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return the product DTO containing product details
     * @throws RuntimeException if the product is not found
     */
    @Override
    public ProductDTO getProductById(int id) {
        log.info("Fetching product with id: {}", id);
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            log.error("Product retrieval failed. Product not found with id: {}", id);
            throw new RuntimeException("product not found with given id: "+id);
        }

        Product product=optionalProduct.get();
        return mapToProductDTO(product);

    }


    /**
     * Retrieves a list of products by their IDs.
     *
     * @param productIds the list of product IDs
     * @return a list of product DTOs containing product details
     * @throws RuntimeException if any product is not found
     */
    @Override
    public List<ProductDTO> getProductListByIds(List<Integer> productIds){

        log.info("Fetching products with ids: {}", productIds);

        List<ProductDTO>productWithCampaignDTOS=new ArrayList<>();

        for (int productId:productIds){

            Optional<Product> optionalProduct=productRepository.findById(productId);

            if (optionalProduct.isPresent()){

                productWithCampaignDTOS.add(mapToProductDTO(optionalProduct.get()));
            }
            else {
                log.error("Product retrieval failed. No product found with id: {}", productId);
                throw new RuntimeException("no such product with given id: "+productId);
            }

        }

        return productWithCampaignDTOS;

    }

    /**
     * Updates the stock of multiple products.
     *
     * @param products the list of products with updated stock information
     * @throws RuntimeException if any product is not found
     */
    @Override
    public void updateStock(List<ProductResponse> products) {
        log.info("Updating stock for products: {}", products.stream().map(ProductResponse::getTitle));
        for (ProductResponse productDTO : products) {
            Product product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() ->{
                        log.error("Product update failed. Product not found with id: {}", productDTO.getId());
                        return new RuntimeException("Product not found");
                    });
            product.setStock(productDTO.getStock());
            productRepository.save(product);
            log.info("Stock updated for product: {}", productDTO.getTitle());
        }
    }

    /**
     * Retrieves a product by its title.
     *
     * @param title the title of the product
     * @return the product DTO containing product details
     * @throws RuntimeException if the product is not found
     */
    @Override
    public ProductDTO getProductByTitle(String title) {
        log.info("Fetching product with title: {}", title);
        Optional<Product>result=productRepository.findProductByTitle(title);

        Product product;

        if (result.isPresent()){
            product= result.get();
        }
        else {
            //we didn't find the product
            log.error("Product retrieval failed. Product not found with title: {}", title);
            throw new RuntimeException("product was not found by title: "+title);
        }

        return mapToProductDTO(product);

    }

    /**
     * Saves a new product or updates an existing product.
     *
     * @param product the product entity to save
     */
    @Override
    public void save(Product product) {
        log.info("Saving product: {}", product);
        productRepository.save(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return a message indicating the result of the deletion
     * @throws RuntimeException if the product is not found
     */
    @Override
    public String deleteById(int id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)){
            log.error("Product deletion failed. No product found with id: {}", id);
            throw new RuntimeException("There is no product with given id: "+id);
        }

        productRepository.deleteById(id);
        return "product with id "+id+" is deleted successfully.";

    }
    /**
     * Sorts products by a specified field.
     *
     * @param field the field to sort by
     * @return a list of sorted products
     */
    @Override
    public List<Product> sortProductByField(String field){
        return productRepository.findAll(Sort.by(Sort.Direction.ASC,field));
    }


    /**
     * Retrieves paginated products.
     *
     * @param offset the offset to start pagination
     * @param pageSize the number of products per page
     * @return a list of paginated products
     */
    @Override
    public List<Product> getPaginatedProducts(int offset, int pageSize) {
        return productRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
    }


    /**
     * Retrieves paginated and sorted products.
     *
     * @param offset the offset to start pagination
     * @param pageSize the number of products per page
     * @param field the field to sort by
     * @return a list of paginated and sorted products
     */

    @Override
    public List<Product>getPaginatedAndSortedProducts(int offset,int pageSize,String field){

        return productRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();

    }


    /**
     * Retrieves all product responses, optionally filtered by a keyword.
     *
     * @param keyword an optional keyword for filtering products
     * @return a list of product responses
     */
    @Override
    public List<ProductResponse> findAllResponses(Optional<String> keyword) {

        List<Product>productList;

        if (keyword.isPresent()){

            productList=productRepository.filter(keyword.get());
        }
        else{
            productList=productRepository.findAll();
        }

        return productList.stream().map(this::mapToProductResponse).toList();

    }
    /**
     * Adds a new product based on the provided product request.
     *
     * @param productRequest the request containing product details
     * @return a message indicating the result of the operation
     * @throws RuntimeException if the category or campaign is invalid
     */

    @Override
    public String addProduct(ProductRequest productRequest) {

        String categoryName=productRequest.getCategory();

        Optional<Category> category=categoryRepository.findByTitle(categoryName);

        if (category.isEmpty()){

            log.error("Product addition failed. Invalid category: {}", categoryName);
            throw new RuntimeException(categoryName+" is invalid category !");
        }

        Product product=new Product(productRequest.getTitle(),productRequest.getPrice(),productRequest.getStock());
        product.setCategory(category.get());

        if (productRequest.getCampaignId()!=null){
            Optional<Campaign> campaign=campaignRepository.findById(productRequest.getCampaignId());

            if (campaign.isEmpty()){

                log.error("Product addition failed. Invalid campaign id: {}", productRequest.getCampaignId());
                throw new RuntimeException(productRequest.getCampaignId()+"is invalid campaignId!");
            }

            product.setCampaign(campaign.get());
        }

        productRepository.save(product);
        log.info("Product added: {}", product.getTitle());
        return "product added !";
    }



    /**
     * Maps a Product entity to a ProductResponse DTO.
     *
     * @param product the product entity
     * @return the product response DTO
     */


    public ProductResponse mapToProductResponse(Product product) {

        String campaignName=null;

        Optional<Campaign>campaign=Optional.ofNullable(product.getCampaign());

        if (campaign.isPresent()){
            campaignName=campaign.get().getTitle();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .stock(product.getStock())
                .price(product.getPrice())
                .title(product.getTitle())
                .campaignName(campaignName)
                .categoryName(product.getCategory().getTitle())
                .build();
    }

    /**
     * Maps a Product entity to a ProductDTO.
     *
     * @param product the product entity
     * @return the product DTO
     */

    public ProductDTO mapToProductDTO(Product product) {

        CampaignDTO campaignDTO=null;

        Optional<Campaign>optionalCampaign=Optional.ofNullable(product.getCampaign());



        if (optionalCampaign.isPresent()){

            Campaign campaign=optionalCampaign.get();


            campaignDTO=CampaignDTO.builder()
                    .id(campaign.getId())
                    .title(campaign.getTitle())
                    .discountPercentage(campaign.getDiscountPercentage())
                    .isOneFreeActive(campaign.isOneFreeActive())
                    .isPercentageActive(campaign.isPercentageActive())
                    .build();
        }

        log.debug("campaignDTO: {}",campaignDTO);

        return ProductDTO.builder()
                .id(product.getId())
                .stock(product.getStock())
                .price(product.getPrice())
                .title(product.getTitle())
                .campaignDTO(campaignDTO)
                .categoryName(product.getCategory().getTitle())
                .build();
    }



}
