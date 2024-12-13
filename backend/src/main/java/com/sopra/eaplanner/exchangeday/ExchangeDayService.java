package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeDayService {
    @Autowired
    private ExchangeDayRepository exchangeDayRepository;

    public ExchangeDayResponseDTO createExchangeDay(ExchangeDayRequestDTO requestBody) {
        ExchangeDay exchangeDayToSave = exchangeDayRepository.save(new ExchangeDay(requestBody));
        return new ExchangeDayResponseDTO(exchangeDayToSave);
    }

    public Iterable<ExchangeDayResponseDTO> getAllExchangeDays() {
        Iterable<ExchangeDay> exchangeDays = exchangeDayRepository.findAll();
        List<ExchangeDayResponseDTO> dtos = new ArrayList<>();
        for (ExchangeDay exchangeDay : exchangeDays) {
            dtos.add(new ExchangeDayResponseDTO(exchangeDay));
        }
        return dtos;
    }

    public Iterable<EventResponseDTO> getExchangeDayEvents(Long id) {
        if (!exchangeDayRepository.existsById(id)) {
            throw new EntityNotFoundException("Exchange Day not found");
        }
        return exchangeDayRepository.findById(id)
                .get()
                .getEvents()
                .stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ExchangeDayResponseDTO getExchangeDayById(Long id) {
        if (!exchangeDayRepository.existsById(id)) {
            throw new EntityNotFoundException("Exchange Day not found");
        }
        return new ExchangeDayResponseDTO(exchangeDayRepository.findById(id).get());
    }

    // can do better
    public ExchangeDayResponseDTO updateExchangeDay(Long id, ExchangeDayRequestDTO requestBody) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Exchange Day not found"));

        exchangeDay.setName(requestBody.getName());
        exchangeDay.setDescription(requestBody.getDescription());
        exchangeDay.setDate(requestBody.getDate());
        exchangeDay.setLocation(requestBody.getLocation());
        exchangeDayRepository.save(exchangeDay);

        return new ExchangeDayResponseDTO(exchangeDay);
    }

    public void deleteExchangeDayById(Long id) {
        exchangeDayRepository.deleteById(id);
    }
}
