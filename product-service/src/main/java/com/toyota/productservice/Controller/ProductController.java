package com.toyota.productservice.Controller;

import com.toyota.productservice.DTOs.ProductRequest;
import com.toyota.productservice.DTOs.ProductResponse;
import com.toyota.productservice.DTOs.ProductDTO;
import com.toyota.productservice.Entity.Product;
import com.toyota.productservice.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;

    }


    @PostMapping("/add")
    public String addProduct(@RequestBody ProductRequest productRequest){

       return productService.addProduct(productRequest);

    }

    @PutMapping("/updateStock")
    public ResponseEntity<Void> updateStock(@RequestBody List<ProductResponse> products) {
        productService.updateStock(products);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/list")
    public List<ProductResponse>getAllProducts(@RequestParam Optional<String> keyword){

        if (keyword.isPresent()){
            log.info("Getting products with search keyword: {}",keyword);
        }


        return productService.findAllResponses(keyword);

    }



    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id){
       return productService.deleteById(id);
    }


    @GetMapping("/sort/{field}")
    public List<Product>sortProducts(@PathVariable String field){

        return productService.sortProductByField(field);

    }

    @GetMapping("/paginate/{offset}/{pageSize}")
    public List<Product> paginateProducts(@PathVariable int offset, @PathVariable int pageSize){

        return productService.getPaginatedProducts(offset,pageSize);


    }



    @GetMapping("/paginateAndSort/{offset}/{pageSize}/{field}")
    public List<Product> paginateAndSortProducts(@PathVariable int offset, @PathVariable int pageSize,@PathVariable String field){

        return productService.getPaginatedAndSortedProducts(offset,pageSize,field);


    }

    @GetMapping("/getById/{id}")
    public ProductDTO getProductById(@PathVariable int id){

        return productService.getProductById(id);

    }

    @GetMapping("/getByTitle/{title}")
    public ProductDTO getProductByTitle(@PathVariable String title){

        return productService.getProductByTitle(title);
    }

    @GetMapping("/getListByIds")
    public List<ProductDTO> getProductListByIds(@RequestParam List<Integer>productIds){

        return productService.getProductListByIds(productIds);

    }



}
