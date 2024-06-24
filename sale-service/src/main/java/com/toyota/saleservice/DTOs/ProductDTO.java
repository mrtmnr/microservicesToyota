package com.toyota.saleservice.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("campaignDTO")
    private CampaignDTO campaignDTO;

    private String title;

    private float price;

    private int stock;
}
