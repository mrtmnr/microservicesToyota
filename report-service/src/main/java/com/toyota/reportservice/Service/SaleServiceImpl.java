package com.toyota.reportservice.Service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.toyota.reportservice.DTOs.EntryDTO;

import com.toyota.reportservice.DTOs.SaleResponse;
import com.toyota.reportservice.Feign.SaleProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


@Service
public class SaleServiceImpl implements SaleService{


    private SaleProxy saleProxy;

    @Autowired
    public SaleServiceImpl(SaleProxy saleProxy) {
        this.saleProxy = saleProxy;
    }

    @Override
    public ResponseEntity<byte[]> generatePdfById(int saleId) throws DocumentException {

        SaleResponse sale=saleProxy.getSaleById(saleId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A6);
        PdfWriter.getInstance(document, baos);
        document.open();

        Paragraph title = new Paragraph("Receipt");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Sale ID: "+saleId));
        document.add(new Paragraph("Cashier: "+sale.getCashierName()));
        document.add(new Paragraph("Date:"+ sale.getDate()));
        document.add(new Paragraph("\n"));



        for (EntryDTO entry: sale.getEntryDTOS()){

            document.add(new Paragraph(" - Product: "+entry.getProductName()+" | Quantity: "+entry.getQuantity()+" | Price: "+entry.getProductPrice()));



            if (entry.isCampaignActive()){
                //document.add(new Paragraph("Applied Campaigns:"));

                document.add(new Paragraph(entry.getCampaignName()+ " | Discount: -"+(entry.getProductPrice()*entry.getQuantity()-entry.getTotalPrice())));

            }


        }

        document.add(new Paragraph("Total Price: "+sale.getTotalPrice()));
        document.add(new Paragraph("Total Received: "+sale.getTotalReceived()));
        document.add(new Paragraph("Change: "+sale.getChange()));
        document.add(new Paragraph("\n"));


        document.add(new Paragraph("Payment Method: "+sale.getPayment()));


        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "receipt.pdf");
        headers.setContentLength(baos.size());

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);


    }
}
