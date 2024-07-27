package com.toyota.reportservice.Service;

import com.itextpdf.text.DocumentException;
import com.toyota.reportservice.DTOs.EntryDTO;
import com.toyota.reportservice.DTOs.SaleResponse;
import com.toyota.reportservice.Feign.SaleProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    ReportService underTest;

    @Mock
    SaleProxy saleProxy;


    @BeforeEach
    void setUp() {
        underTest=new ReportServiceImpl(saleProxy);
    }

    @Test
    public void shouldCreateInvoiceWhenSaleIdIsValid() throws DocumentException {
        // Arrange
        int saleId=1;

        EntryDTO entry1=new EntryDTO("pillow",3,true,"1 buy 1 free",900,300);


        List<EntryDTO> entries=new ArrayList<>();

        entries.add(entry1);

        SaleResponse saleResponse=new SaleResponse(1,"mert",entries,950,1000,50,new Date(),"CARD");

        given(saleProxy.getSaleById(saleId)).willReturn(saleResponse);
        // Act
        ResponseEntity<byte[]> response = underTest.generatePdfById(saleId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getBody());
    }


    @Test
    public void shouldNotCreateInvoiceWhenSaleIdIsInvalid() {
        // Arrange
        int saleId = 999;
        given(saleProxy.getSaleById(saleId)).willThrow(new RuntimeException("Sale not found"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            underTest.generatePdfById(saleId);
        });

        assertEquals("Sale not found", exception.getMessage());
    }

}