package com.sopra.eaplanner.controller;

import com.sopra.eaplanner.model.ExchangeDay;
import com.sopra.eaplanner.service.ExchangeDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/exchange-days")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService exchangeDayService;

    @GetMapping
    public Iterable<ExchangeDay> getAllExchangeDays() {
        return exchangeDayService.getAllExchangeDays();
    }

    @GetMapping("/{id}")
    public Optional<ExchangeDay> getExchangeDayById(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayById(id);
    }

    @PostMapping
    public ExchangeDay createExchangeDay(@RequestBody ExchangeDay exchangeDay) {
        return exchangeDayService.createExchangeDay(exchangeDay);
    }

    @PutMapping("/{id}")
    public ExchangeDay updateExchangeDay(@PathVariable Long id, @RequestBody ExchangeDay exchangeDay) {
        return exchangeDayService.updateExchangeDay(id, exchangeDay);
    }

    @DeleteMapping("/{id}")
    public void deleteExchangeDay(@PathVariable Long id) {
        exchangeDayService.deleteExchangeDayById(id);
    }

}
