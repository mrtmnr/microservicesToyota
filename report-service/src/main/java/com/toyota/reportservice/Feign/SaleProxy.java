package com.toyota.reportservice.Feign;


import com.toyota.reportservice.DTOs.SaleResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


@FeignClient(name = "sale-service")
public interface SaleProxy {

    @GetMapping("/sale/getSaleById/{saleId}")
    public SaleResponse getSaleById(@PathVariable int saleId);

    @GetMapping("/sale/list")
    public List<SaleResponse> getAllSales(@RequestParam Optional<String> keyword);


    @GetMapping("/sale/sort/{field}")
    public List<SaleResponse>sortSales(@PathVariable String field);


    @GetMapping("/sale/paginate/{offset}/{pageSize}")
    public List<SaleResponse> paginateSales(@PathVariable int offset, @PathVariable int pageSize);




    @GetMapping("/sale/paginateAndSort/{offset}/{pageSize}/{field}")
    public List<SaleResponse> paginateAndSortSales(@PathVariable int offset, @PathVariable int pageSize,@PathVariable String field);



}
