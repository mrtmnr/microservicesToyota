package com.toyota.reportservice.Feign;


import com.toyota.reportservice.DTOs.SaleResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "sale-service")
public interface SaleProxy {

    @GetMapping("/sale/getSaleById/{saleId}")
    public SaleResponse getSaleById(@PathVariable int saleId);

}
