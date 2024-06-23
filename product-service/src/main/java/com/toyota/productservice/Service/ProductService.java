package com.toyota.productservice.Service;



import com.toyota.productservice.DTOs.ProductRequest;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.DTOs.ProductWithCampaignDTO;
import com.toyota.productservice.Entity.Product;

import java.util.List;
import java.util.Optional;


public interface ProductService {

    public ProductWithCampaignDTO getProductById(int id);
    List<Product>findAll();

    Product getProductByTitle(String title);

    void save(Product product);

    String deleteById(int id);

    List<Product> sortProductByField(String field);

    List<Product> getPaginatedProducts(int offset, int pageSize);

    List<Product>getPaginatedAndSortedProducts(int offset,int pageSize,String field);


    List<ProductResponse> findAllResponses(Optional<String> keyword);

    String addProduct(ProductRequest productRequest);
}
