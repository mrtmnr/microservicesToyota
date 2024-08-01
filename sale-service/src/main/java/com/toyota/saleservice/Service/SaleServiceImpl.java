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

import java.util.*;

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

    /**
     * Saves a sale.
     *
     * @param sale the sale entity to save
     */

    public void save(Sale sale) {
        saleRepository.save(sale);
    }

    /**
     * Finds all sales.
     *
     * @return a list of all sales
     */
    @Override
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves all sale responses, optionally filtered by a keyword.
     *
     * @param keyword an optional keyword for filtering sales
     * @return a list of sale responses
     */

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

    /**
     * Retrieves a sale by its ID.
     *
     * @param id the ID of the sale
     * @return the sale entity
     * @throws RuntimeException if the sale is not found
     */
    @Override
    public Sale findById(int id) {

        Optional<Sale>result=saleRepository.findById(id);

        Sale sale;

        if (result.isPresent()){

            sale=result.get();

        }
        else {
            log.error("Sale not found !");
            throw new RuntimeException("invalid sale id !");
        }

        return  sale;
    }


    /**
     * Maps a Sale entity to a SaleResponse DTO.
     *
     * @param sale the sale entity
     * @return the sale response DTO
     */
    @Override
    public SaleResponse mapToSaleResponse(Sale sale) {


        List<EntryDTO> entryDTOs =new ArrayList<>();


        List<Entry>entries=sale.getCheckout().getEntries();

        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        int index=0;

        for(Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);

            index++;

            String campaignName=null;


            if (entry.isCampaignActive()){
                log.debug("adding campaign name to saleResponse");
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

        }

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

    /**
     * Sorts sales by a specified field.
     *
     * @param field the field to sort by
     * @return a list of sorted sales
     */
    @Override
    public List<SaleResponse> sortSaleByField(String field) {

        log.info("Sorting sales by field: {}", field);

        List<Sale>sales;


        if(field.equals("totalPrice")){

            sales= saleRepository.findAllOrderByCheckoutTotalPriceAsc();

        }
        else{
            sales= saleRepository.findAll(Sort.by(Sort.Direction.ASC,field));
        }

        log.debug("Sorted {} sales by field: {}", sales.size(), field);

        return sales.stream().map(this::mapToSaleResponse).toList();
    }

    /**
     * Retrieves paginated sales.
     *
     * @param offset the offset to start pagination
     * @param pageSize the number of sales per page
     * @return a list of paginated sales
     */
    @Override
    public List<SaleResponse> getPaginatedSales(int offset, int pageSize) {
        log.info("Fetching paginated sales with offset: {} and pageSize: {}", offset, pageSize);
        List<Sale>sales= saleRepository.findAll(PageRequest.of(offset,pageSize)).get().toList();
        log.debug("Fetched {} sales for pagination.", sales.size());
        return sales.stream().map(this::mapToSaleResponse).toList();
    }

    /**
     * Retrieves paginated and sorted sales.
     *
     * @param offset the offset to start pagination
     * @param pageSize the number of sales per page
     * @param field the field to sort by
     * @return a list of paginated and sorted sales
     */
    @Override
    public List<SaleResponse> getPaginatedAndSortedSales(int offset, int pageSize, String field) {
        log.info("Fetching paginated and sorted sales with offset: {}, pageSize: {}, and field: {}", offset, pageSize, field);
        List<Sale>sales= saleRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field))).get().toList();
        log.debug("Fetched {} sales for pagination and sorting.", sales.size());
        return sales.stream().map(this::mapToSaleResponse).toList();

    }

    /**
     * Retrieves products from a list of entries.
     *
     * @param entries the list of entries
     * @return a list of product DTOs
     */

    public List<ProductDTO>getProductsFromEntries(List<Entry>entries){

        List<Integer>productIds=new ArrayList<>();

        for (Entry entry:entries){

            productIds.add(entry.getProductId());
            log.debug("product with id: {} added.",entry.getProductId());
        }


        return productProxy.getProductListByIds(productIds);


    }

    /**
     * Adds a product to the checkout by its title.
     *
     * @param productTitle the title of the product to add
     * @return a message indicating the total price of the checkout
     * @throws RuntimeException if the product is out of stock
     */

    @Override
    public String addToCheckout(String productTitle) {

        ProductDTO product=productProxy.getProductByTitle(productTitle);

        log.info(product.getTitle()+" will be added to checkout!");


        Checkout checkout=checkoutRepository.findLastCheckout();


        if (product.getStock()==0){
            log.error("Product out of stock: {}", productTitle);
            throw new RuntimeException("This product is out of stock !");
        }

        boolean isProductAlreadyExist=false;

        if (checkout.getEntries()!=null){
            log.debug("Checking if product already exists in checkout");
            //if there is a match this means our product is multiple from now on, so we just increment the quantity of our existing entry
            Optional<Entry>matchedEntry=checkout.getEntries().stream().filter(e -> e.getProductId()==product.getId()).findFirst();
            if (matchedEntry.isPresent()){
                Entry entry= matchedEntry.get();

                entry.setQuantity(entry.getQuantity()+1);
                isProductAlreadyExist=true;

                log.debug("Incremented quantity to {} for product: {}",entry.getQuantity(),productTitle);

            }
        }

        //create new related entry if there is no existing already
        if (!isProductAlreadyExist){

            log.debug("creating new entry!");
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
            log.debug("retrieved product from entry in addToCheckout: "+ entryProduct.getTitle());

            index++;

            Optional<CampaignDTO> campaign= Optional.ofNullable(entryProduct.getCampaignDTO());
            if (campaign.isPresent()){
                CampaignDTO campaign1=entryProduct.getCampaignDTO();

                if (campaign1.isOneFreeActive()){
                    float percentage= campaign1.getDiscountPercentage();
                    int buyCount= (int)(100/percentage);
                    int quantity= entry.getQuantity();

                    if (quantity>=buyCount){
                        int freeCount=quantity/buyCount;
                        float priceWithCampaign=entryProduct.getPrice()*(entry.getQuantity()-freeCount);
                        entry.setTotalPrice(priceWithCampaign);
                        entry.setCampaignActive(true);
                        log.info("Enough amount oneFree campaign applied for {}",entryProduct.getTitle());

                    }
                    else{
                        entry.setTotalPrice(entryProduct.getPrice());
                        log.info("Insufficient amount for oneFree campaign being applied for {}",entryProduct.getTitle());
                    }

                }
                if (campaign1.isPercentageActive()){
                    float percentage=campaign1.getDiscountPercentage();
                    float price=entryProduct.getPrice()*entry.getQuantity();
                    float priceWithCampaign=price*((100-percentage)/100);
                    entry.setTotalPrice(priceWithCampaign);
                    entry.setCampaignActive(true);
                    log.info("Percentage campaign applied for {}",entryProduct.getTitle());

                }

            }
            else{
                float price=entryProduct.getPrice();
                int quantity=entry.getQuantity();
                entry.setTotalPrice(price*quantity);
                log.info("No campaign applied for {}",entryProduct.getTitle());

            }

        }

        float checkoutPrice= (float) checkout.getEntries().stream().mapToDouble(Entry::getTotalPrice).sum();
        log.info("totalPrice: {}",checkoutPrice);

        checkout.setTotalPrice(checkoutPrice);

        checkoutRepository.save(checkout);

        return "total price: "+ checkoutPrice;

    }

    /**
     * Finalizes a sale with the given payment details.
     *
     * @param totalReceived the total amount received from the customer
     * @param payment the payment method (card or cash)
     * @param username the username of the cashier
     * @return a message indicating the sale has been saved
     * @throws RuntimeException if the checkout is empty or there are insufficient funds
     */

    @Override
    public String sell(float totalReceived, String payment, String username) {

        Checkout checkout=checkoutRepository.findLastCheckout();

        Sale sale=new Sale();

        List<Entry>entries=checkout.getEntries();

        if (entries==null){
            log.error("Checkout is empty, cannot proceed with sale.");
            throw new RuntimeException("Checkout is empty!");
        }

        float totalPrice=checkout.getTotalPrice();

        sale.setUsername(username);
        sale.setTotalReceived(totalReceived);



        if (totalPrice>totalReceived) {
            log.error("Insufficient funds: totalPrice={}, totalReceived={}", totalPrice, totalReceived);
            throw new RuntimeException("Insufficient funds! Payment cancelled!");

        }



        switch (payment) {
            case "card" -> sale.setPayment(EnumPayment.CARD);
            case "cash" -> sale.setPayment(EnumPayment.CASH);
            default -> {
                log.error("Invalid payment method: {}", payment);
                throw new RuntimeException("Invalid payment method!");
            }
        }

        sale.setDate(new Date());


        List<ProductDTO>entryProducts= getProductsFromEntries(entries);

        int index=0;

        //decrement stocks
        for(Entry entry:entries){

            ProductDTO entryProduct=entryProducts.get(index);
            log.debug("retrieved product from entry in sell: "+ entryProduct.getTitle());

            index++;

            entryProduct.setStock(entryProduct.getStock()-entry.getQuantity());
            log.info(entryProduct.getTitle()+" stock decremented to {}",entryProduct.getStock());
        }

        //update products in product-service
        productProxy.updateStock(entryProducts);


        sale.setCheckout(checkout);

        Checkout nextCheckout=new Checkout();

        checkoutRepository.save(nextCheckout);


        saleRepository.save(sale);


        return "sale has been saved - "+ sale;
    }

    /**
     * Retrieves a sale response by sale ID.
     *
     * @param saleId the ID of the sale
     * @return the sale response DTO
     * @throws RuntimeException if the sale is not found
     */

    @Override
    public SaleResponse getSaleResponseBySaleId(int saleId) {

        Sale sale;

        Optional<Sale>optionalSale=saleRepository.findById(saleId);

        if (optionalSale.isPresent()){

            sale=optionalSale.get();

        }
        else{
            log.error("There is no Sale with given id !");
            throw new RuntimeException("There is no Sale with given id: "+saleId);
        }


        return mapToSaleResponse(sale);


    }




}
