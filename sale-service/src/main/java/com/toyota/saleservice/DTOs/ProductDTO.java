package com.toyota.saleservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private int id;
    private String categoryName;

    private String campaignName;

    private String title;

    private float price;

    private int stock;
}
