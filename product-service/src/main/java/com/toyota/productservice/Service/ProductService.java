package com.toyota.productservice.Service;



import com.toyota.productservice.DTOs.ProductRequest;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.DTOs.ProductDTO;
import com.toyota.productservice.Entity.Product;

import java.util.List;
import java.util.Optional;


public interface ProductService {

    public ProductDTO getProductById(int id);
    List<Product>findAll();

    ProductDTO getProductByTitle(String title);

    void save(Product product);

    String deleteById(int id);

    List<ProductResponse> sortProductByField(String field);

    List<ProductResponse> getPaginatedProducts(int offset, int pageSize);

    List<ProductResponse>getPaginatedAndSortedProducts(int offset,int pageSize,String field);


    List<ProductResponse> findAllResponses(Optional<String> keyword);

    String addProduct(ProductRequest productRequest);

    List<ProductDTO> getProductListByIds(List<Integer> productIds);

    void updateStock(List<ProductResponse> products);

    ProductResponse mapToProductResponse(Product product);

    ProductDTO mapToProductDTO(Product product);
}
