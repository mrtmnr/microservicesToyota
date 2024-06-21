package com.toyota.productservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignDTO {

    private int id;


    private String title;


    private float discountPercentage;


    private boolean isPercentageActive;


    private boolean isOneFreeActive;


}
