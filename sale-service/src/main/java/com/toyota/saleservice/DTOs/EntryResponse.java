package com.toyota.saleservice.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntryResponse {

    private String productName;

    private int quantity;

    private float productPrice;


}
