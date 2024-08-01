package com.toyota.saleservice.Controller;

import com.toyota.saleservice.DTOs.SaleResponse;
import com.toyota.saleservice.Service.SaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/sale")
public class SaleController {


    private SaleService saleService;



    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;

    }

    @PostMapping("/addToCheckout/{productName}")
    public String  addToCheckout(@PathVariable String productName){

        return saleService.addToCheckout(productName);
    }


    @PostMapping("/sell/{totalReceived}/{payment}")
    public String sell(@PathVariable float totalReceived,@PathVariable String payment,@RequestHeader String username){

       return saleService.sell(totalReceived, payment,username);

    }

    @GetMapping("/getSaleById/{saleId}")
    public SaleResponse getSaleById(@PathVariable int saleId){

        return saleService.getSaleResponseBySaleId(saleId);

    }


    @GetMapping("/list")
    public List<SaleResponse> getAllSales(@RequestParam Optional<String> keyword){



        if (keyword.isPresent()){
            log.info("search keyword: {}",keyword.get());
        }


        return saleService.findAllResponses(keyword);
    }



    @GetMapping("/sort/{field}")
    public List<SaleResponse>sortSales(@PathVariable String field){

        return saleService.sortSaleByField(field);

    }

    @GetMapping("/paginate/{offset}/{pageSize}")
    public List<SaleResponse> paginateSales(@PathVariable int offset, @PathVariable int pageSize){

        return saleService.getPaginatedSales(offset,pageSize);

    }



    @GetMapping("/paginateAndSort/{offset}/{pageSize}/{field}")
    public List<SaleResponse> paginateAndSortSales(@PathVariable int offset, @PathVariable int pageSize,@PathVariable String field){

        return saleService.getPaginatedAndSortedSales(offset,pageSize,field);


    }





}