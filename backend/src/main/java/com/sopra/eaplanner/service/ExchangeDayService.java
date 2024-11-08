package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.ExchangeDay;
import com.sopra.eaplanner.repository.ExchangeDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExchangeDayService {
    @Autowired
    private ExchangeDayRepository repository;

    public ExchangeDay createExchangeDay(ExchangeDay exchangeDay) {
        return repository.save(exchangeDay);
    }

    public Iterable<ExchangeDay> getAllExchangeDays(){
        return repository.findAll();
    }

    public Optional<ExchangeDay> getExchangeDayById(Long id) {
        return repository.findById(id);
    }

    // can do better
    public ExchangeDay updateExchangeDay(Long id, ExchangeDay exchangeDayUpdates) {
        return repository.save(exchangeDayUpdates);
    }

    public void deleteExchangeDayById(Long id){
        repository.deleteById(id);
    }

}
