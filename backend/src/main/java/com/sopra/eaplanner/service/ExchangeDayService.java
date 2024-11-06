package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.ExchangeDay;
import com.sopra.eaplanner.repository.ExchangeDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeDayService {
    @Autowired
    private ExchangeDayRepository repository;

    public int saveExchangeDay(ExchangeDay exchangeDay) {
        return repository.save(exchangeDay);
    }

    public List<ExchangeDay> getAllExchangeDays() {
        return repository.findAll();
    }

    public ExchangeDay getExchangeDayById(Long id) {
        return repository.findById(id);
    }
}
