package com.toyota.productservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private String category;

    private String title;

    private int price;

    private int stock;

    private Integer campaignId;
}
