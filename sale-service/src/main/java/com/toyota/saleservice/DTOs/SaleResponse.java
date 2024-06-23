package com.toyota.saleservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {

    private int saleId;

    private String cashierName;

    private List<EntryResponse> entryResponses;

    private float totalPrice;

    private float totalReceived;

    private float change;

    private List<AppliedCampaignResponse> appliedCampaignResponses;

    private Date date;

    private String payment;
}
