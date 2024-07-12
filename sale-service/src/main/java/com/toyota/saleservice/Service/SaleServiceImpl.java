package com.toyota.saleservice.Service;
import com.toyota.saleservice.DTOs.*;
import com.toyota.saleservice.Enum.EnumPayment;
import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Entity.Entry;
import com.toyota.saleservice.Entity.Sale;
import com.toyota.saleservice.Feign.ProductProxy;
import com.toyota.saleservice.Repository.CheckoutRepository;
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

    final private SaleRepository saleRepository;

    final private ProductProxy productProxy;

    final private CheckoutRepository checkoutRepository;


    @Autowired
    public SaleServiceImpl( ProductProxy productProxy,SaleRepository saleRepository, CheckoutRepository checkoutRepository) {
        this.saleRepository = saleRepository;
        this.productProxy=productProxy;
        this.checkoutRepository = checkoutRepository;
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




    @Override
    public SaleResponse mapToSaleResponse(Sale sale) {


        List<EntryDTO> entryDTOs =new ArrayList<>();


        List<Entry>entries=sale.getCheckout().getEntries();

        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        //log.info("entryProduct: "+ entryProducts.get(0));

        int index=0;

        for(Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);

            index++;

            String campaignName=null;


            if (entry.isCampaignActive()){
                campaignName=entryProduct.getCampaignDTO().getTitle();
            }

           EntryDTO entryDTO = EntryDTO.builder()
                    .productName(entryProduct.getTitle())
                    .quantity(entry.getQuantity())
                    .campaignActive(entry.isCampaignActive())
                    .campaignName(campaignName)
                    .productPrice(entryProduct.getPrice())
                    .totalPrice(entry.getTotalPrice())
                    .build();



           entryDTOs.add(entryDTO);


        };



        return SaleResponse.builder()
                .saleId(sale.getId())
                .cashierName(sale.getUsername())
                .date(sale.getDate())
                .payment(sale.getPayment().toString())
                .totalPrice(sale.getCheckout().getTotalPrice())
                .totalReceived(sale.getTotalReceived())
                .entryDTOs(entryDTOs)
                .change(sale.getTotalReceived()-sale.getCheckout().getTotalPrice())
                .build();

    }

    @Override
    public List<SaleResponse> sortSaleByField(String field) {

        List<Sale>sales=new ArrayList<>();


        if(field.equals("totalPrice")){

            sales= saleRepository.findAllOrderByCheckoutTotalPriceAsc();

        }
        else{
            sales= saleRepository.findAll(Sort.by(Sort.Direction.ASC,field));
        }


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

    public List<ProductDTO>getProductsFromEntries(List<Entry>entries){

        List<Integer>productIds=new ArrayList<>();

        for (Entry entry:entries){

            productIds.add(entry.getProductId());

        }


        return productProxy.getProductListByIds(productIds);


    }



    @Override
    public String addToCheckout(String productTitle) {


        ProductDTO product=productProxy.getProductByTitle(productTitle);

        log.info(product+" will be added to checkout!");


        Checkout checkout=checkoutRepository.findLastCheckout();


        if (product.getStock()==0){
            throw new RuntimeException("This product is out of stock !");
        }

        boolean isProductAlreadyExist=false;

        if (checkout.getEntries()!=null){

            log.info("incrementing quantity of product!");
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

            log.info("creating new entry!");
            Entry entry=new Entry();
            entry.setQuantity(1);
            entry.setProductId(product.getId());
            checkout.addEntry(entry);

        }


        List<Entry>entries=checkout.getEntries();

        //using productProxy here
        List<ProductDTO>entryProducts=getProductsFromEntries(entries);

        int index=0;

        for (Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);

            System.out.println(entryProducts.get(index));

            index++;

            Optional<CampaignDTO> campaign= Optional.ofNullable(entryProduct.getCampaignDTO());
            if (campaign.isPresent()){
                CampaignDTO campaign1=entryProduct.getCampaignDTO();
               // log.info("Campaign: "+campaign1.getTitle());
                if (campaign1.isOneFreeActive()){
                    float percentage= campaign1.getDiscountPercentage();
                    int buyCount= (int)(100/percentage);
                    int quantity= entry.getQuantity();

                    if (quantity>=buyCount){
                        int freeCount=quantity/buyCount;
                        float priceWithCampaign=entryProduct.getPrice()*(entry.getQuantity()-freeCount);
                        entry.setTotalPrice(priceWithCampaign);
                        entry.setCampaignActive(true);
                        log.info("Enough amount oneFree campaign applied!");

                    }
                    else{
                        entry.setTotalPrice(entryProduct.getPrice());
                        log.info("Insufficient amount for oneFree campaign being applied!");
                    }

                }
                if (campaign1.isPercentageActive()){
                    float percentage=campaign1.getDiscountPercentage();
                    float price=entryProduct.getPrice()*entry.getQuantity();
                    float priceWithCampaign=price*((100-percentage)/100);
                    entry.setTotalPrice(priceWithCampaign);
                    entry.setCampaignActive(true);
                    log.info("Percentage campaign applied!");

                }

            }
            else{
                float price=entryProduct.getPrice();
                int quantity=entry.getQuantity();
                entry.setTotalPrice(price*quantity);

            }

        };


        float checkoutPrice= (float) checkout.getEntries().stream().mapToDouble(Entry::getTotalPrice).sum();
        log.info("totalPrice: {}",checkoutPrice);

        checkout.setTotalPrice(checkoutPrice);


        checkoutRepository.save(checkout);




        return "total price: "+ checkoutPrice;



    }

    @Override
    public String sell(float totalReceived, String payment, String username) {


        Checkout checkout=checkoutRepository.findLastCheckout();

        Sale sale=new Sale();

        List<Entry>entries=checkout.getEntries();

        if (entries==null){
            throw new RuntimeException("Checkout is empty!");
        }


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

        //update products in product-service
        productProxy.updateStock(entryProducts);


        sale.setCheckout(checkout);

        Checkout nextCheckout=new Checkout();

        checkoutRepository.save(nextCheckout);


        saleRepository.save(sale);


        return "sale has been saved - "+ sale;
    }

    @Override
    public SaleResponse getSaleResponseBySaleId(int saleId) {

        Sale sale=null;

        Optional<Sale>optionalSale=saleRepository.findById(saleId);

        if (optionalSale.isPresent()){

            sale=optionalSale.get();

        }
        else{
            throw new RuntimeException("There is no Sale with given id: "+saleId);
        }


        return mapToSaleResponse(sale);


    }




}
