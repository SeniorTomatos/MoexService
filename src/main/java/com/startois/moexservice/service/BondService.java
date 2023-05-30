package com.startois.moexservice.service;

import com.startois.moexservice.dto.BondDto;
import com.startois.moexservice.dto.StocksDto;
import com.startois.moexservice.dto.TickersDto;
import com.startois.moexservice.exception.LimitRequestsException;
import com.startois.moexservice.model.Currency;
import com.startois.moexservice.model.Stock;
import com.startois.moexservice.moexClient.CorporateBondsClient;
import com.startois.moexservice.moexClient.GovBondsClient;
import com.startois.moexservice.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BondService {

    private final CorporateBondsClient corparateBondsClient;
    private final GovBondsClient govBondsClient;

    private final Parser parser;

    public StocksDto getBondsFromMoex(TickersDto tickersDto) {
        List<BondDto> allBonds = new ArrayList<>();
        allBonds.addAll(getCorporateBonds());
        allBonds.addAll(getGovBonds());


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

    public List<BondDto> getCorporateBonds() {
        String xmlFromMoex = corparateBondsClient.getBondsFromMoex();
        List<BondDto> bonds = parser.parse(xmlFromMoex);
        if(bonds.isEmpty()) {
            log.error("Moex isn't answering for getting corporate bonds.");
            throw new LimitRequestsException("Moex isn't answering for getting corporate bonds.");
        }
        return bonds;
    }

    public List<BondDto> getGovBonds() {
        String xmlFromMoex = govBondsClient.getBondsFromMoex();

        List<BondDto> bonds = parser.parse(xmlFromMoex);
        if(bonds.isEmpty()) {
            log.error("Moex isn't answering for getting government bonds.");
            throw new LimitRequestsException("Moex isn't answering for getting government bonds.");
        }
        return bonds;
    }


}
