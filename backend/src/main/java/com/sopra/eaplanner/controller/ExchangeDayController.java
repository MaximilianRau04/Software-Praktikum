package com.sopra.eaplanner.controller;

import com.sopra.eaplanner.model.ExchangeDay;
import com.sopra.eaplanner.service.ExchangeDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-days")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService service;

    @GetMapping
    public List<ExchangeDay> getAllExchangeDays() {
        return service.getAllExchangeDays();
    }

    @PostMapping
    public int createExchangeDay(@RequestBody ExchangeDay exchangeDay) {
        return service.saveExchangeDay(exchangeDay);
    }

    @GetMapping("/{id}")
    public ExchangeDay getExchangeDayById(@PathVariable Long id) {
        return service.getExchangeDayById(id);
    }
}
