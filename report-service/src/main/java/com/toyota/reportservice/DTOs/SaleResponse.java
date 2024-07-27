package com.toyota.reportservice.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("entryDTOs")
    private List<EntryDTO> entryDTOs;

    private float totalPrice;

    private float totalReceived;

    private float change;

    private Date date;

    private String payment;
}
