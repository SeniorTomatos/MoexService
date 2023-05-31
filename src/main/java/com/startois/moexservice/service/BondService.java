package com.startois.moexservice.service;

import com.startois.moexservice.dto.*;
import com.startois.moexservice.exception.BondNotFoundException;
import com.startois.moexservice.exception.LimitRequestsException;
import com.startois.moexservice.model.Currency;
import com.startois.moexservice.model.Stock;
import com.startois.moexservice.moexClient.CorporateBondsClient;
import com.startois.moexservice.moexClient.GovBondsClient;
import com.startois.moexservice.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BondService {

    private final BondRepository bondRepository;

    private final CacheManager cacheManager;

    public StocksDto getBondsFromMoex(TickersDto tickersDto) {
        List<BondDto> allBonds = new ArrayList<>();
        allBonds.addAll(bondRepository.getCorporateBonds());
        allBonds.addAll(bondRepository.getGovBonds());


        List<BondDto> resultBonds = allBonds.stream()
                .filter(b -> tickersDto.getTickers().contains(b.getTicker()))
                .collect(Collectors.toList());

        List<Stock> stocks = resultBonds.stream()
                .map(b -> {
                    return Stock.builder()
                            .ticker(b.getTicker())
                            .name(b.getName())
                            .figi(b.getTicker())
                            .type("Bond")
                            .currency(Currency.RUB)
                            .source("MOEX")
                            .build();
                })
                .collect(Collectors.toList());
        return new StocksDto(stocks);
    }


    public StockPricesDto getPricesByFigies(FigiesDto figiesDto) {
        log.info("Request for figies {}", figiesDto.getFigies());
        List<String> figies = new ArrayList<>(figiesDto.getFigies());
        List<BondDto> allBonds = new ArrayList<>();
        allBonds.addAll(bondRepository.getGovBonds());
        allBonds.addAll(bondRepository.getCorporateBonds());
        figies.removeAll(allBonds.stream()
                .map(b -> b.getTicker()).collect(Collectors.toList()));
        if (!figies.isEmpty()) {
            throw new BondNotFoundException(String.format("Bonds %s not found.", figies));
        }
        List<StockPrice> prices = allBonds.stream()
                .filter(b -> figiesDto.getFigies().contains(b.getTicker()))
                .map(b -> new StockPrice(b.getTicker(), b.getPrice() * 10))
                .collect(Collectors.toList());
        return new StockPricesDto(prices);
    }
}
