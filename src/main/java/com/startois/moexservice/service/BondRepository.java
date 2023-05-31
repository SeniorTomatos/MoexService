package com.startois.moexservice.service;

import com.startois.moexservice.dto.BondDto;
import com.startois.moexservice.exception.LimitRequestsException;
import com.startois.moexservice.moexClient.CorporateBondsClient;
import com.startois.moexservice.moexClient.GovBondsClient;
import com.startois.moexservice.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BondRepository {

    private final CorporateBondsClient corparateBondsClient;
    private final GovBondsClient govBondsClient;
    private final Parser parser;

    @Cacheable(value = "corps")
    public List<BondDto> getCorporateBonds() {
        log.info("Getting corporate bonds from Moex");
        String xmlFromMoex = corparateBondsClient.getBondsFromMoex();
        List<BondDto> bonds = parser.parse(xmlFromMoex);
        if(bonds.isEmpty()) {
            log.error("Moex isn't answering for getting corporate bonds.");
            throw new LimitRequestsException("Moex isn't answering for getting corporate bonds.");
        }
        return bonds;
    }

    @Cacheable(value = "govs")
    public List<BondDto> getGovBonds() {
        log.info("Getting government bonds from Moex");
        String xmlFromMoex = govBondsClient.getBondsFromMoex();

        List<BondDto> bonds = parser.parse(xmlFromMoex);
        if(bonds.isEmpty()) {
            log.error("Moex isn't answering for getting government bonds.");
            throw new LimitRequestsException("Moex isn't answering for getting government bonds.");
        }
        return bonds;
    }
}
