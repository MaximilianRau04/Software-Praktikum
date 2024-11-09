package com.sopra.eaplanner.exchangeday;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sopra.eaplanner.event.Event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeDayService {
    @Autowired
    private ExchangeDayRepository exchangeDayRepository;

    public ExchangeDay createExchangeDay(ExchangeDay exchangeDay) {
        return exchangeDayRepository.save(exchangeDay);
    }

    public Iterable<ExchangeDay> getAllExchangeDays(){
        return exchangeDayRepository.findAll();
    }

    public Optional<ExchangeDay> getExchangeDayById(Long id) {
        return exchangeDayRepository.findById(id);
    }

    // can do better
    public ExchangeDay updateExchangeDay(Long id, ExchangeDay exchangeDayUpdates) {
        return exchangeDayRepository.save(exchangeDayUpdates);
    }

    public void deleteExchangeDayById(Long id){
        exchangeDayRepository.deleteById(id);
    }

    // Methode, um alle Event-IDs für ein bestimmtes ExchangeDay zu holen
    public ExchangeDayDTO getExchangeDayWithEventIds(Long exchangeDayId) {
        // Hole das ExchangeDay
        ExchangeDay exchangeDay = exchangeDayRepository.findById(exchangeDayId)
                .orElseThrow(() -> new RuntimeException("ExchangeDay nicht gefunden"));

        // Hole die Event-IDs
        List<Long> eventIds = exchangeDay.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Erstelle das DTO und gebe es zurück
        return new ExchangeDayDTO(
                exchangeDay.getId(),
                exchangeDay.getDate(),
                exchangeDay.getName(),
                exchangeDay.getLocation(),
                exchangeDay.getDescription(),
                eventIds
        );
    }
}
