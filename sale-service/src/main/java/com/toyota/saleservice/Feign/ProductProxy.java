package com.toyota.saleservice.Feign;

import com.toyota.saleservice.DTOs.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductProxy {

    @GetMapping("/product/getById/{id}")
    public ProductDTO getProductById(@PathVariable int id);

    @GetMapping("/product/getByTitle/{title}")
    public ProductDTO getProductByTitle(@PathVariable String title);

    @GetMapping("/product/getListByIds")
    public List<ProductDTO> getProductListByIds(@RequestParam List<Integer>productIds);


}
