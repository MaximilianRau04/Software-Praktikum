package com.sopra.eaplanner.exchangeday;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService exchangeDayService;

    @GetMapping("/exchange-days")
    public Iterable<ExchangeDay> getAllExchangeDays() {
        return exchangeDayService.getAllExchangeDays();
    }

    @GetMapping("/exchange-days/{id}")
    public ExchangeDayDTO getExchangeDayById(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayWithEventIds(id);
    }

    @PostMapping("/exchange-days")
    public ExchangeDay createExchangeDay(@Valid @RequestBody ExchangeDay requestBody) {
        return exchangeDayService.createExchangeDay(requestBody);
    }

    @PutMapping("/exchange-days/{id}")
    public ExchangeDay updateExchangeDay(@PathVariable Long id, @RequestBody ExchangeDay requestBody) {
        return exchangeDayService.updateExchangeDay(id, requestBody);
    }

    @DeleteMapping("/exchange-days/{id}")
    public void deleteExchangeDay(@PathVariable Long id) {
        exchangeDayService.deleteExchangeDayById(id);
    }

}
