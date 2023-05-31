package com.startois.moexservice.controller;

import com.startois.moexservice.dto.FigiesDto;
import com.startois.moexservice.dto.StockPricesDto;
import com.startois.moexservice.dto.StocksDto;
import com.startois.moexservice.dto.TickersDto;
import com.startois.moexservice.service.BondService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bonds")
public class MoexBondController {
    private final BondService bondService;
    @PostMapping("/getBondsByTickers")
    public StocksDto getBondsFromMoex(@RequestBody TickersDto tickersDto){
     return bondService.getBondsFromMoex(tickersDto);
    }

    @PostMapping("/getBondsByFigies")
    public StockPricesDto getPricesByFigies(@RequestBody FigiesDto figiesDto) {
        return bondService.getPricesByFigies(figiesDto);
    }

}
