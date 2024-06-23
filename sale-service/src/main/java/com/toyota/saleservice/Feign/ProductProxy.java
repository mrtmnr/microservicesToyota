package com.toyota.saleservice.Feign;

import com.toyota.saleservice.DTOs.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service",url = "localhost:8081")
public interface ProductProxy {

    @GetMapping("/getById/{id}")
    public ProductDTO getProductById(@PathVariable int id);

    @GetMapping("/getByTitle/{title}")
    public ProductDTO getProductByTitle(@PathVariable String title);


}
