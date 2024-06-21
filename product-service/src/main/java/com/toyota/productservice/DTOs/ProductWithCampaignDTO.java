package com.toyota.productservice.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductWithCampaignDTO {
    private int id;
    private String categoryName;

    private CampaignDTO campaignDTO;

    private String title;

    private float price;

    private int stock;
}
