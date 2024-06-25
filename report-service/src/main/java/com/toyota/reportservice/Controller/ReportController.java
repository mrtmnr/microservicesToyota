package com.toyota.reportservice.Controller;

import com.itextpdf.text.DocumentException;
import com.toyota.reportservice.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private ReportService reportService;


    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generatePdf/{saleId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable int saleId) throws IOException, DocumentException {

        return reportService.generatePdfById(saleId);
    }



}
