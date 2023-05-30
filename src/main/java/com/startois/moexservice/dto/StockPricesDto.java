package com.startois.moexservice.dto;

import lombok.Value;

import java.util.List;

@Value
public class StockPricesDto {

    List<StockPrice> stockPrices;
}
