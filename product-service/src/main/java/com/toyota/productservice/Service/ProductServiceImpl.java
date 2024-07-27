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

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }


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

    @Override
    public void save(Product product) {
        log.info("Saving product: {}", product);
        productRepository.save(product);
    }

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

    @Override
    public List<Product> sortProductByField(String field){
        return productRepository.findAll(Sort.by(Sort.Direction.ASC,field));
    }

    @Override
    public List<Product> getPaginatedProducts(int offset, int pageSize) {
        return productRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
    }



    @Override
    public List<Product>getPaginatedAndSortedProducts(int offset,int pageSize,String field){

        return productRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();

    }

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
