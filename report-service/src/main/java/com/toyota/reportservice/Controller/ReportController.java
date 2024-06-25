package com.toyota.reportservice.Controller;

import com.itextpdf.text.DocumentException;
import com.toyota.reportservice.DTOs.SaleResponse;
import com.toyota.reportservice.Feign.SaleProxy;
import com.toyota.reportservice.Service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    private ReportService reportService;

    private SaleProxy saleProxy;

    @Autowired
    public ReportController(ReportService reportService, SaleProxy saleProxy) {
        this.reportService = reportService;
        this.saleProxy = saleProxy;
    }



    @GetMapping("/generatePdf/{saleId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable int saleId) throws IOException, DocumentException {

        return reportService.generatePdfById(saleId);
    }

    @GetMapping("/listSales")
    public List<SaleResponse> getAllSales(@RequestParam Optional<String> keyword){


        return saleProxy.getAllSales(keyword);

    }



    @GetMapping("/sortSales/{field}")
    public List<SaleResponse>sortSales(@PathVariable String field){

        return saleProxy.sortSales(field);

    }

    @GetMapping("/paginateSales/{offset}/{pageSize}")
    public List<SaleResponse> paginateSales(@PathVariable int offset, @PathVariable int pageSize){

        return saleProxy.paginateSales(offset,pageSize);


    }


    @GetMapping("/paginateAndSortSales/{offset}/{pageSize}/{field}")
    public List<SaleResponse> paginateAndSortSales(@PathVariable int offset, @PathVariable int pageSize,@PathVariable String field){

        return saleProxy.paginateAndSortSales(offset,pageSize,field);


    }





}
