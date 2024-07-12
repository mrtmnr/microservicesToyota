package com.toyota.saleservice.Service;

import com.toyota.saleservice.DTOs.CampaignDTO;
import com.toyota.saleservice.DTOs.EntryDTO;
import com.toyota.saleservice.DTOs.ProductDTO;
import com.toyota.saleservice.DTOs.SaleResponse;
import com.toyota.saleservice.Entity.Checkout;
import com.toyota.saleservice.Entity.Entry;
import com.toyota.saleservice.Entity.Sale;
import com.toyota.saleservice.Enum.EnumPayment;
import com.toyota.saleservice.Feign.ProductProxy;
import com.toyota.saleservice.Repository.CheckoutRepository;
import com.toyota.saleservice.Repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    SaleService underTest;

    @Mock
    SaleRepository saleRepository;

    @Mock
    CheckoutRepository checkoutRepository;

    @Mock
    ProductProxy productProxy;

    @Captor
    private ArgumentCaptor<List<ProductDTO>> productListCaptor;

    @BeforeEach
    void setUp() {

        underTest=new SaleServiceImpl(productProxy,saleRepository,checkoutRepository);

    }

    @Test
    void save() {
        //given
        Sale sale=new Sale("mert",200,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(192);
        Entry entry1=new Entry(3,123,true);
        Entry entry2=new Entry(1,78,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);
        checkout.setEntries(entries);
        sale.setCheckout(checkout);

        //when
        underTest.save(sale);

        //then
        verify(saleRepository).save(sale);
    }

    @Test
    void findAll() {
        //when
        underTest.findAll();
        //then
        verify(saleRepository).findAll();

    }

    @Test
    void returnAllResponsesWithoutFilter() {
        //given
        Optional<String>keyword=Optional.empty();

        //when
        underTest.findAllResponses(keyword);

        //then
        verify(saleRepository).findAll();

    }

    @Test
    void returnResponsesWithFilter() {
        //given
        Entry entry1=new Entry(3,120,true);
        Entry entry2=new Entry(1,104,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);

        Sale sale=new Sale("mert",200,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(200);

        checkout.setEntries(entries);
        sale.setCheckout(checkout);

        ProductDTO product1 = new ProductDTO(1, "fruit",new CampaignDTO(1,"%20 sale", 20, true, false) ,"apple",40,60);

        ProductDTO product2 = new ProductDTO(2, "fruit",null,"watermelon",104,70);

        when(productProxy.getProductListByIds(Arrays.asList(1, 2))).thenReturn(Arrays.asList(product1, product2));


        Optional<String>keyword=Optional.of("mert");


        when(saleRepository.filter(keyword.get())).thenReturn(List.of(sale));


        //when
        List<SaleResponse> saleResponses = underTest.findAllResponses(keyword);

        //then
        assertNotNull(saleResponses);
        verify(saleRepository).filter(keyword.get());
        verify(saleRepository, never()).findAll();
    }

    @Test
    void returnSaleByById() {
        //given
        int id=1;

        Sale sale=new Sale("mert",220,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(200);
        sale.setCheckout(checkout);

        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));

        //when
        Sale foundSale=underTest.findById(id);

        //then
        assertThat(foundSale).isEqualTo(sale);

    }

    @Test
    void throwExceptionAndDontReturnSaleWithInvalidId() {
        //given
        int id=1;

        when(saleRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid sale id !");

    }



    @Test
    void ShouldAddFirstProductWithoutCampaign() {
        // given
        String productTitle="apple";

        Checkout checkout=new Checkout();

        ProductDTO productDTO = new ProductDTO(1,"fruit",null,"apple",40,55);
        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        // when
        String result = underTest.addToCheckout(productTitle);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo("total price: "+40f);
        verify(checkoutRepository).save(checkout);

    }

    @Test
    void ShouldIncrementQuantityWhenAddExistingProduct() {
        // given
        String productTitle="apple";

        Checkout checkout=new Checkout(40);

        Entry entry1=new Entry(1,40,false);
        entry1.setProductId(1);
        List<Entry> entries=new ArrayList<>();
        entries.add(entry1);
        checkout.setEntries(entries);

        ProductDTO productDTO = new ProductDTO(1,"fruit",null,"apple",40,55);
        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        // when
        String result = underTest.addToCheckout(productTitle);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo("total price: "+80f);

        ArgumentCaptor<Checkout>checkoutArgumentCaptor=ArgumentCaptor.forClass(Checkout.class);
        verify(checkoutRepository).save(checkoutArgumentCaptor.capture());
        Checkout capturedCheckout=checkoutArgumentCaptor.getValue();

        assertThat(capturedCheckout.getEntries().get(0).getQuantity()).isEqualTo(checkout.getEntries().get(0).getQuantity());
        assertThat(capturedCheckout.getEntries().get(0).getProductId()).isEqualTo(checkout.getEntries().get(0).getProductId());
    }

    @Test
    void ShouldAddFirstProductToCheckoutAndShouldNotApplyOneFreeCampaign() {
        // given
        String productTitle="apple";

        Checkout checkout=new Checkout();

        CampaignDTO campaignDTO = new CampaignDTO(1,"two buy one free", 50, false, true);
        ProductDTO productDTO = new ProductDTO(1,"electronics",campaignDTO,"tv",4000,155);
        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        // when
        String result = underTest.addToCheckout(productTitle);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo("total price: "+4000f);
        verify(checkoutRepository).save(checkout);

    }


    @Test
    void ShouldAddProductToCheckoutAndShouldApplyOneFreeCampaign() {
        // given
        String productTitle="apple";

        Checkout checkout=new Checkout();

        Entry entry1=new Entry(1,4000,false);
        entry1.setProductId(1);
        List<Entry> entries=new ArrayList<>();
        entries.add(entry1);
        checkout.setEntries(entries);


        CampaignDTO campaignDTO = new CampaignDTO(1,"two buy one free", 50, false, true);
        ProductDTO productDTO = new ProductDTO(1,"electronics",campaignDTO,"tv",4000,155);
        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        // when
        String result = underTest.addToCheckout(productTitle);

        // then
        assertNotNull(result);

        ArgumentCaptor<Checkout>checkoutArgumentCaptor=ArgumentCaptor.forClass(Checkout.class);
        verify(checkoutRepository).save(checkoutArgumentCaptor.capture());
        Checkout capturedCheckout=checkoutArgumentCaptor.getValue();


        assertThat(capturedCheckout.getTotalPrice()).isEqualTo(4000f);
        assertThat(capturedCheckout.getEntries().get(0).getQuantity()).isEqualTo(2);
        assertThat(capturedCheckout.getEntries().get(0).getProductId()).isEqualTo(entry1.getProductId());
        assertThat(capturedCheckout.getEntries().get(0).isCampaignActive()).isEqualTo(true);

    }

    @Test
    void ShouldAddFirstProductToCheckoutAndPercentageCampaignIsActive() {
        // given
        String productTitle="apple";

        Checkout checkout=new Checkout();

        CampaignDTO campaignDTO = new CampaignDTO(1,"%10 sale", 10, true, false);
        ProductDTO productDTO = new ProductDTO(1,"fruit",campaignDTO,"apple",40,55);
        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        // when
        String result = underTest.addToCheckout(productTitle);

        // then
        assertNotNull(result);
        assertThat(result).isEqualTo("total price: "+36f);
        verify(checkoutRepository).save(checkout);

    }

    @Test
    void shouldThrowExceptionWhenProductIsOutOfStock() {

        String productTitle="apple";

        Checkout checkout=new Checkout();

        ProductDTO productDTO = new ProductDTO(1,"fruit",null,"apple",40,0);

        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);
        when(productProxy.getProductByTitle(productTitle)).thenReturn(productDTO);

        assertThatThrownBy(()->
                underTest.addToCheckout(productTitle))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("This product is out of stock !");

        verify(checkoutRepository,never()).save(checkout);

    }

    @Test
    void shouldSellWhenTotalReceivedIsSufficientAndPaymentValid(){
        float totalReceived=200;
        String payment="card";
        String username="mert";
        int stock=55;

        Entry entry1=new Entry(3,120,true);
        Entry entry2=new Entry(1,104,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);

        Checkout checkout=new Checkout(200);

        checkout.setEntries(entries);

        CampaignDTO campaignDTO1 = new CampaignDTO(1,"%20 sale", 20, true, false);
        ProductDTO productDTO1 = new ProductDTO(1,"fruit",campaignDTO1,"apple",40,stock);

        ProductDTO productDTO2 = new ProductDTO(2,"fruit",null,"watermelon",104,stock);


        List<ProductDTO> productDTOs = Arrays.asList(productDTO1, productDTO2);


        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);
        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);

        //when
        String result=underTest.sell(totalReceived,payment,username);

        //then
        verify(productProxy).updateStock(productDTOs);
        verify(checkoutRepository).save(any());

        ArgumentCaptor<Sale>saleArgumentCaptor=ArgumentCaptor.forClass(Sale.class);
        verify(saleRepository).save(saleArgumentCaptor.capture());
        Sale capturedSale=saleArgumentCaptor.getValue();

        assertThat(capturedSale.getUsername()).isEqualTo(username);
        assertThat(capturedSale.getCheckout()).isEqualTo(checkout);
        assertThat(capturedSale.getTotalReceived()).isEqualTo(totalReceived);


        verify(productProxy).updateStock(productListCaptor.capture());
        List<ProductDTO> capturedProducts=productListCaptor.getValue();

        assertThat(capturedProducts.get(0).getStock()).isEqualTo(stock-capturedSale.getCheckout().getEntries().get(0).getQuantity());
        assertThat(capturedProducts.get(1).getStock()).isEqualTo(stock-capturedSale.getCheckout().getEntries().get(1).getQuantity());


    }

    @Test
    void shouldThrowExceptionWhenCheckoutIsEmpty() {
        float totalReceived=200;
        String payment="card";
        String username="mert";

        Checkout checkout=new Checkout(200);

        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);
        //when

        assertThatThrownBy(()->
                underTest.sell(totalReceived,payment,username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Checkout is empty!");


        verify(productProxy,never()).updateStock(anyList());
        verify(saleRepository,never()).save(any());
        verify(checkoutRepository,never()).save(any());

    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {

        float totalReceived=100;
        String payment="cash";
        String username="mert";

        Checkout checkout=new Checkout(200);

        Entry entry1=new Entry(3,120,true);
        Entry entry2=new Entry(1,104,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);

        checkout.setEntries(entries);


        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);
        //whe

        assertThatThrownBy(()->
                underTest.sell(totalReceived,payment,username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("insufficient funds ! - Payment cancelled !");


        verify(productProxy,never()).updateStock(anyList());
        verify(saleRepository,never()).save(any());
        verify(checkoutRepository,never()).save(any());

    }

    @Test
    void shouldThrowExceptionWhenInvalidPaymentMethod() {

        float totalReceived=200;
        String payment="paypal";
        String username="mert";

        Checkout checkout=new Checkout(200);

        Entry entry1=new Entry(3,120,true);
        Entry entry2=new Entry(1,104,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);

        checkout.setEntries(entries);


        when(checkoutRepository.findLastCheckout()).thenReturn(checkout);
        //whe

        assertThatThrownBy(()->
                underTest.sell(totalReceived,payment,username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid payment method !");


        verify(productProxy,never()).updateStock(anyList());
        verify(saleRepository,never()).save(any());
        verify(checkoutRepository,never()).save(any());

    }



    @Test
    @Disabled
    void sortSaleByField() {


    }

    @Test
    @Disabled
    void getPaginatedSales() {
    }

    @Test
    @Disabled
    void getPaginatedAndSortedSales() {
    }

    @Test

    void returnSaleResponseBySaleId(){
        //given
        int saleId=1;

        Entry entry1=new Entry(3,120,true);

        entry1.setProductId(1);

        List<Entry> entries=new ArrayList<>();

        entries.add(entry1);

        Sale sale=new Sale("mert",100,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(96);

        checkout.setEntries(entries);
        sale.setCheckout(checkout);

        CampaignDTO campaignDTO1 = new CampaignDTO(1,"%20 sale", 20, true, false);
        ProductDTO productDTO1 = new ProductDTO(1,"fruit",campaignDTO1,"apple",40,55);

        List<ProductDTO> productDTOs=new ArrayList<>();
        productDTOs.add(productDTO1);

        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);

        //when
        SaleResponse saleResponse=underTest.getSaleResponseBySaleId(saleId);

        //then
        assertThat(saleResponse.getTotalPrice()).isEqualTo(sale.getCheckout().getTotalPrice());
        assertThat(saleResponse.getDate()).isEqualTo(sale.getDate());
        assertThat(saleResponse.getPayment()).isEqualTo(sale.getPayment().toString());
        assertThat(saleResponse.getChange()).isEqualTo(sale.getTotalReceived()-sale.getCheckout().getTotalPrice());


        EntryDTO entryDTO = saleResponse.getEntryDTOs().get(0);

        assertThat(entryDTO.getProductName()).isEqualTo(productDTO1.getTitle());
        assertThat(entryDTO.getTotalPrice()).isEqualTo(entry1.getTotalPrice());
        assertThat(entryDTO.getProductPrice()).isEqualTo(productDTO1.getPrice());
        assertThat(entryDTO.getQuantity()).isEqualTo(entry1.getQuantity());
        assertThat(entryDTO.getCampaignName()).isEqualTo(productDTO1.getCampaignDTO().getTitle());

    }

    @Test
    void throwExceptionAndDontReturnSaleResponseWithInvalidId() {
        //given
        int id=1;

        when(saleRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(()->
                underTest.getSaleResponseBySaleId(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There is no Sale with given id: "+id);


    }



    @Test
    void shouldMapSaleToSaleResponseEntryToEntryDTO() {

        Entry entry1=new Entry(3,120,true);
        Entry entry2=new Entry(1,104,false);
        entry1.setProductId(1);
        entry2.setProductId(2);
        List<Entry> entries= Arrays.asList(entry1,entry2);

        Sale sale=new Sale("mert",200,new Date(), EnumPayment.CARD);
        Checkout checkout=new Checkout(200);

        checkout.setEntries(entries);
        sale.setCheckout(checkout);

        CampaignDTO campaignDTO1 = new CampaignDTO(1,"%20 sale", 20, true, false);
        ProductDTO productDTO1 = new ProductDTO(1,"fruit",campaignDTO1,"apple",40,55);


        ProductDTO productDTO2 = new ProductDTO(2,"fruit",null,"watermelon",104,70);


        List<ProductDTO> productDTOs = Arrays.asList(productDTO1, productDTO2);


        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);


        // when
        SaleResponse saleResponse = underTest.mapToSaleResponse(sale);

        // then
        assertNotNull(saleResponse);
        //assertEquals(sale.getId(), saleResponse.getSaleId());
        assertEquals(sale.getUsername(), saleResponse.getCashierName());
        assertEquals(sale.getDate(), saleResponse.getDate());
        assertEquals(sale.getPayment().toString(), saleResponse.getPayment());
        assertEquals(sale.getCheckout().getTotalPrice(), saleResponse.getTotalPrice());
        assertEquals(sale.getTotalReceived(), saleResponse.getTotalReceived());
        assertThat(saleResponse.getChange()).isEqualTo(sale.getTotalReceived()-sale.getCheckout().getTotalPrice());

        List<EntryDTO> entryDTOs = saleResponse.getEntryDTOs();

        assertNotNull(entryDTOs);
        assertEquals(2, entryDTOs.size());

        EntryDTO entryDTO = entryDTOs.get(0);

        assertThat(entryDTO.getProductName()).isEqualTo(productDTO1.getTitle());
        assertThat(entryDTO.getTotalPrice()).isEqualTo(entry1.getTotalPrice());
        assertThat(entryDTO.getProductPrice()).isEqualTo(productDTO1.getPrice());
        assertThat(entryDTO.getQuantity()).isEqualTo(entry1.getQuantity());
        assertThat(entryDTO.getCampaignName()).isEqualTo(productDTO1.getCampaignDTO().getTitle());


        EntryDTO entryDTO2 = entryDTOs.get(1);

        assertThat(entryDTO2.getProductName()).isEqualTo(productDTO2.getTitle());
        assertThat(entryDTO2.getTotalPrice()).isEqualTo(entry2.getTotalPrice());
        assertThat(entryDTO2.getProductPrice()).isEqualTo(productDTO2.getPrice());
        assertThat(entryDTO2.getQuantity()).isEqualTo(entry2.getQuantity());
        assertNull(entryDTO2.getCampaignName());



    }
    @Test
    void shouldReturnProductsFromEntryList() {
        //given
        Entry entry1=new Entry(3,123,true);
        Entry entry2=new Entry(1,78,false);
        entry1.setProductId(1);
        entry2.setProductId(2);

        List<Entry>entries=Arrays.asList(entry1,entry2);

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1);
        productDTO1.setTitle("Product1");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2);
        productDTO2.setTitle("Product2");

        List<ProductDTO> productDTOs = Arrays.asList(productDTO1, productDTO2);

        when(productProxy.getProductListByIds(anyList())).thenReturn(productDTOs);

        // when
        List<ProductDTO> result = underTest.getProductsFromEntries(entries);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(productDTO1, result.get(0));
        assertEquals(productDTO2, result.get(1));

        verify(productProxy).getProductListByIds(Arrays.asList(1, 2));



    }





}