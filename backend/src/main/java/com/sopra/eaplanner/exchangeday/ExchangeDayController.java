package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/exchange-days")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService exchangeDayService;

    @GetMapping("")
    public Iterable<ExchangeDayResponseDTO> getAllExchangeDays() {
        return exchangeDayService.getAllExchangeDays();
    }

    @GetMapping("/{id}")
    public ExchangeDayResponseDTO getExchangeDayById(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayById(id);
    }

    @GetMapping("/{id}/events")
    public Iterable<EventResponseDTO> getExchangeDayEvents(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayEvents(id);
    }

    @PostMapping("")
    public ResponseEntity<ExchangeDayResponseDTO> createExchangeDay(@Valid @RequestBody ExchangeDayRequestDTO requestBody) {
        ExchangeDayResponseDTO savedDTO = exchangeDayService.createExchangeDay(requestBody);
        URI location = URI.create("/api/exchange-days/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
    }

    @PutMapping("/{id}")
    public ExchangeDayResponseDTO updateExchangeDay(@PathVariable Long id, @RequestBody ExchangeDayRequestDTO requestBody) {
        return exchangeDayService.updateExchangeDay(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExchangeDay(@PathVariable Long id) {
        exchangeDayService.deleteExchangeDayById(id);
        return ResponseEntity.noContent().build();
    }
}
