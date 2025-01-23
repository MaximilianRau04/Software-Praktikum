package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.locations.Location;
import com.sopra.eaplanner.locations.LocationRepository;
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
    @Autowired
    private LocationRepository locationRepository;

    public ExchangeDayResponseDTO createExchangeDay(ExchangeDayRequestDTO requestBody) {
        System.out.println("ID: "+ requestBody.getLocationId());
        Location location = locationRepository.findById(requestBody.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        ExchangeDay exchangeDayToSave = new ExchangeDay(requestBody, location);
        exchangeDayRepository.save(exchangeDayToSave);
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
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Exchange day not found"));

        return exchangeDay.getEvents()
                .stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ExchangeDayResponseDTO getExchangeDayById(Long id) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Exchange day not found"));

        return new ExchangeDayResponseDTO(exchangeDay);
    }

    public ExchangeDayResponseDTO updateExchangeDay(Long id, ExchangeDayRequestDTO requestBody) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Exchange Day not found"));
        Location location = locationRepository.findById(requestBody.getLocationId())
                .orElseThrow(()->new EntityNotFoundException("Location not found"));
        exchangeDay.setName(requestBody.getName());
        exchangeDay.setDescription(requestBody.getDescription());
        exchangeDay.setStartDate(requestBody.getStartDate());
        exchangeDay.setEndDate(requestBody.getEndDate());
        exchangeDay.setLocation(location);
        exchangeDayRepository.save(exchangeDay);

        return new ExchangeDayResponseDTO(exchangeDay);
    }

    public void deleteExchangeDayById(Long id) {
        exchangeDayRepository.deleteById(id);
    }
}
