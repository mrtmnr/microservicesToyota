package com.toyota.saleservice.Service;


import com.toyota.saleservice.DTOs.ProductDTO;
import com.toyota.saleservice.DTOs.SaleResponse;
import com.toyota.saleservice.Entity.Entry;
import com.toyota.saleservice.Entity.Sale;



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
    String addToCheckout(String productTitle);

    String sell(float totalReceived, String payment,String username);

    SaleResponse getSaleResponseBySaleId(int saleId);

    SaleResponse mapToSaleResponse(Sale sale);

    List<ProductDTO>getProductsFromEntries(List<Entry>entries);

}
