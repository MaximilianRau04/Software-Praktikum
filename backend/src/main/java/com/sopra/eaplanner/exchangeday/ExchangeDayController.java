package com.sopra.eaplanner.exchangeday;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-days")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService exchangeDayService;

    @GetMapping("")
    public Iterable<ExchangeDay> getAllExchangeDays() {
        return exchangeDayService.getAllExchangeDays();
    }

    @GetMapping("/{id}")
    public ExchangeDayDTO getExchangeDayById(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayWithEventIds(id);
    }

    @PostMapping("")
    public ExchangeDay createExchangeDay(@Valid @RequestBody ExchangeDay requestBody) {
        return exchangeDayService.createExchangeDay(requestBody);
    }

    @PutMapping("/{id}")
    public ExchangeDay updateExchangeDay(@PathVariable Long id, @RequestBody ExchangeDay requestBody) {
        return exchangeDayService.updateExchangeDay(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public void deleteExchangeDay(@PathVariable Long id) {
        exchangeDayService.deleteExchangeDayById(id);
    }

}
