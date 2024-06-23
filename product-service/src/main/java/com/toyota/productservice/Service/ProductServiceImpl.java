package com.toyota.productservice.Service;


import com.toyota.productservice.DTOs.CampaignDTO;
import com.toyota.productservice.DTOs.ProductRequest;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.DTOs.ProductWithCampaignDTO;
import com.toyota.productservice.Entity.Campaign;
import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Entity.Product;
import com.toyota.productservice.Repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private CategoryService categoryService;

    private CampaignService campaignService;


    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, CampaignService campaignService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.campaignService = campaignService;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }


    @Override
    public ProductWithCampaignDTO getProductById(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()){
            throw new RuntimeException("product not found with given id: "+id);
        }

        Product product=optionalProduct.get();
        return mapToProductWithCampaignDTO(product);

    }

    @Override
    public List<ProductWithCampaignDTO> getProductListByIds(List<Integer> productIds) {

        List<ProductWithCampaignDTO>productWithCampaignDTOS=new ArrayList<>();

        for (int productId:productIds){

            Product product=productRepository.findById(productId).get();


            productWithCampaignDTOS.add(mapToProductWithCampaignDTO(product));

        }

        return productWithCampaignDTOS;

    }



    @Override
    public ProductWithCampaignDTO getProductByTitle(String title) {

        Optional<Product>result=productRepository.findProductByTitle(title);

        Product product;

        if (result.isPresent()){
            product= result.get();
        }
        else {
            //we didn't find the product
            throw new RuntimeException("product was not found by title: "+title);
        }

        return mapToProductWithCampaignDTO(product);

    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public String deleteById(int id) {
        productRepository.deleteById(id);
        return "product with id "+id+" is deleted successfully.";

    }

    @Override
    public List<Product> sortProductByField(String field) {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC,field));
    }

    @Override
    public List<Product> getPaginatedProducts(int offset, int pageSize) {
        return productRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
    }



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
        Category category=categoryService.findByTitle(categoryName);


        Product product=new Product(productRequest.getTitle(),productRequest.getPrice(),productRequest.getStock());
        product.setCategory(category);

        if (productRequest.getCampaignId()!=0){
            Campaign campaign=campaignService.findById(productRequest.getCampaignId());
            product.setCampaign(campaign);
        }

        productRepository.save(product);

        return "product added !";
    }


    private ProductResponse mapToProductResponse(Product product) {

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


    private ProductWithCampaignDTO mapToProductWithCampaignDTO(Product product) {

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

        return ProductWithCampaignDTO.builder()
                .id(product.getId())
                .stock(product.getStock())
                .price(product.getPrice())
                .title(product.getTitle())
                .campaignDTO(campaignDTO)
                .categoryName(product.getCategory().getTitle())
                .build();
    }



}
