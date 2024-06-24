package com.toyota.saleservice.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntryDTO {

    private String productName;

    private int quantity;

    private boolean campaignActive;

    private String campaignName;
    private float totalPrice;

    private float productPrice;


}
