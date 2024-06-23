package com.toyota.saleservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliedCampaignResponse {

    private String campaignName;

    private String productName;

    private float discountAmount;

}
