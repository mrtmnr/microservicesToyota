package com.toyota.reportservice.Service;

import com.itextpdf.text.DocumentException;
import org.springframework.http.ResponseEntity;

public interface SaleService {


    ResponseEntity<byte[]> generatePdfById(int saleId) throws DocumentException;
}
