package com.toyota.saleservice.Service;

import com.toyota.saleservice.DTOs.SaleResponse;
import com.toyota.saleservice.Entity.Sale;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Optional;

public interface SaleService {
    void save(Sale sale);

    List<Sale> findAll();

    List<SaleResponse>findAllResponses(Optional<String> keyword);

    Sale findById(int id);


    List<SaleResponse> sortSaleByField(String field);

    List<SaleResponse> getPaginatedSales(int offset, int pageSize);

    List<SaleResponse>getPaginatedAndSortedSales(int offset,int pageSize,String field);
    String addToCheckout(String productName);

    String sell(float totalReceived, String payment);

    ResponseEntity<byte[]> generatePdfById(int saleId) throws DocumentException;

}
