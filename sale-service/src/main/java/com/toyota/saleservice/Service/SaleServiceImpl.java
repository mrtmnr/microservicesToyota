package com.toyota.saleservice.Service;

import com.toyota.saleservice.DTOs.*;
import com.toyota.saleservice.Enum.EnumPayment;
import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Entity.Entry;
import com.toyota.saleservice.Entity.Sale;
import com.toyota.saleservice.Feign.ProductProxy;
import com.toyota.saleservice.Repository.SaleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SaleServiceImpl implements SaleService {

    private SaleRepository saleRepository;

    private ProductProxy productProxy;

    private CheckoutService checkoutService;


    @Autowired
    public SaleServiceImpl( ProductProxy productProxy,SaleRepository saleRepository, CheckoutService checkoutService) {
        this.saleRepository = saleRepository;
        this.productProxy=productProxy;
        this.checkoutService = checkoutService;
    }


    public void save(Sale sale) {
        saleRepository.save(sale);
    }

    @Override
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    @Override
    public List<SaleResponse> findAllResponses(Optional<String> keyword) {

        List<Sale>saleList;

        if(keyword.isPresent()){

            saleList=saleRepository.filter(keyword.get());
        }
        else {
            saleList=saleRepository.findAll();
        }

        return saleList.stream().map(this::mapToSaleResponse).toList();

    }

    @Override
    public Sale findById(int id) {

        Optional<Sale>result=saleRepository.findById(id);;

        Sale sale;

        if (result.isPresent()){

            sale=result.get();

        }
        else {
            throw new RuntimeException("invalid sale id !");
        }

        return  sale;
    }



    private SaleResponse mapToSaleResponse(Sale sale) {


        List<EntryResponse>entryResponses=new ArrayList<>();

        List<AppliedCampaignResponse>appliedCampaignResponses=new ArrayList<>();


        List<Entry>entries=sale.getCheckout().getEntries();

        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        int index=0;

        for(Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);

            index++;

           EntryResponse entryResponse= EntryResponse.builder()
                    .productName(entryProduct.getTitle())
                    .quantity(entry.getQuantity())
                    .productPrice(entryProduct.getPrice())
                    .build();

           entryResponses.add(entryResponse);

            if (entry.isCampaignActive()){



              AppliedCampaignResponse appliedCampaignResponse=  AppliedCampaignResponse.builder()
                        .campaignName(entryProduct.getCampaign().getTitle())
                        .productName(entryProduct.getTitle())
                        .discountAmount(entryProduct.getPrice()*entry.getQuantity()-entry.getTotalPrice())
                        .build();

              appliedCampaignResponses.add(appliedCampaignResponse);

            }


        };



        return SaleResponse.builder()
                .saleId(sale.getId())
                .cashierName(sale.getUsername())
                .date(sale.getDate())
                .payment(sale.getPayment().toString())
                .totalPrice(sale.getCheckout().getTotalPrice())
                .totalReceived(sale.getTotalReceived())
                .appliedCampaignResponses(appliedCampaignResponses)
                .entryResponses(entryResponses)
                .build();

    }



    @Override
    public List<SaleResponse> sortSaleByField(String field) {
        List<Sale>sales= saleRepository.findAll(Sort.by(Sort.Direction.ASC,field));
        return sales.stream().map(this::mapToSaleResponse).toList();
    }

    @Override
    public List<SaleResponse> getPaginatedSales(int offset, int pageSize) {
        List<Sale>sales= saleRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
        return sales.stream().map(this::mapToSaleResponse).toList();
    }

    @Override
    public List<SaleResponse> getPaginatedAndSortedSales(int offset, int pageSize, String field) {
        List<Sale>sales= saleRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();
        return sales.stream().map(this::mapToSaleResponse).toList();

    }

    private List<ProductDTO>getProductsFromEntries(List<Entry>entries){

        List<Integer>productIds=new ArrayList<>();

        for (Entry entry:entries){

            productIds.add(entry.getProductId());

        }


        return productProxy.getProductListByIds(productIds);


    }


    @Override
    public String addToCheckout(String productTitle) {


        ProductDTO product=productProxy.getProductByTitle(productTitle);


        Checkout checkout=checkoutService.getLatestCheckout();


        if (product.getStock()==0){
            throw new RuntimeException("this Product out of stock !");
        }


        boolean isProductAlreadyExist=false;

        if (checkout.getEntries()!=null){

            //if there is a match this means our product is multiple from now on so we just increment the quantity of our existing entry
            Optional<Entry>matchedEntry=checkout.getEntries().stream().filter(e -> e.getProductId()==product.getId()).findFirst();
            if (matchedEntry.isPresent()){
                Entry entry= matchedEntry.get();

                entry.setQuantity(entry.getQuantity()+1);
                isProductAlreadyExist=true;
            }
        }

        //create new related entry if there is no existing already
        if (!isProductAlreadyExist){

            Entry entry=new Entry();
            entry.setQuantity(1);
            entry.setProductId(product.getId());
            checkout.addEntry(entry);

        }



        List<Entry>entries=checkout.getEntries();

        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        int index=0;

        for (Entry entry:entries){


            ProductDTO entryProduct=entryProducts.get(index);

            index++;

            Optional<CampaignDTO> campaign= Optional.ofNullable(entryProduct.getCampaign());
            if (campaign.isPresent()){
                CampaignDTO campaign1=entryProduct.getCampaign();
                if (campaign1.isOneFreeActive()){

                    float percentage= campaign1.getDiscountPercentage();
                    int buyCount= (int)(100/percentage);
                    int quantity= entry.getQuantity();

                    if (quantity>=buyCount){
                        int freeCount=quantity/buyCount;
                        float priceWithCampaign=entryProduct.getPrice()*(entry.getQuantity()-freeCount);
                        entry.setTotalPrice(priceWithCampaign);
                        entry.setCampaignActive(true);

                    }
                    else{
                        entry.setTotalPrice(entryProduct.getPrice());
                    }

                }
                if (campaign1.isPercentageActive()){
                    float percentage=campaign1.getDiscountPercentage();
                    float price=entryProduct.getPrice()*entry.getQuantity();
                    float priceWithCampaign=price*((100-percentage)/100);
                    entry.setTotalPrice(priceWithCampaign);
                    entry.setCampaignActive(true);
                }

            }
            else{
                float price=entryProduct.getPrice();
                int quantity=entry.getQuantity();
                entry.setTotalPrice(price*quantity);

            }

        };

        float checkoutPrice= (float) checkout.getEntries().stream().mapToDouble(Entry::getTotalPrice).sum();
        checkout.setTotalPrice(checkoutPrice);


        checkoutService.save(checkout);




        return "total price: "+ checkoutPrice;



    }

    @Override
    public String sell(float totalReceived, String payment, String username) {


        Checkout checkout=checkoutService.getLatestCheckout();

        Sale sale=new Sale();


        List<Entry>entries=checkout.getEntries();


        float totalPrice=checkout.getTotalPrice();

        sale.setUsername(username);
        sale.setTotalReceived(totalReceived);



        if (totalPrice>totalReceived) {

            throw new RuntimeException("insufficient funds ! - Payment cancelled !");
        }



        switch (payment) {
            case "card" -> sale.setPayment(EnumPayment.CARD);
            case "cash" -> sale.setPayment(EnumPayment.CASH);
            default -> throw new RuntimeException("invalid payment method !");
        }

        sale.setDate(new Date());


        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        int index=0;

        //decrement stocks

        for(Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);

            index++;

            entryProduct.setStock(entryProduct.getStock()-entry.getQuantity());

        };

        sale.setCheckout(checkout);



        saleRepository.save(sale);




        return "sale has been saved - "+ sale;
    }


/*
    @Override
    public ResponseEntity<byte[]> generatePdfById(int saleId) throws DocumentException {

        Sale sale=findById(saleId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A6);
        PdfWriter.getInstance(document, baos);
        document.open();

        Paragraph title = new Paragraph("Receipt");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Sale ID: "+saleId));
        document.add(new Paragraph("Cashier: "+sale.getUser().getUsername()));
        document.add(new Paragraph("Date:"+ sale.getDate()));
        document.add(new Paragraph("\n"));


        for (Entry e: sale.getEntries()){

            document.add(new Paragraph(" - Product: "+e.getProduct().getTitle()+" | Quantity: "+e.getQuantity()+" | Price: "+e.getProduct().getPrice()));

            Optional<AppliedCampaign> appliedCampaign=Optional.ofNullable(e.getAppliedCampaign());

            if (appliedCampaign.isPresent()){
                //document.add(new Paragraph("Applied Campaigns:"));

                document.add(new Paragraph(e.getAppliedCampaign().getCampaign().getTitle()+ " | Discount: -"+(e.getProduct().getPrice()*e.getQuantity()-e.getTotalPrice())));

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
*/

}
