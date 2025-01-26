package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controller for managing ExchangeDay entities in the system.
 * Handles HTTP requests for creating, reading, updating, and deleting ExchangeDay records.
 * Provides endpoints for retrieving all ExchangeDays, a single ExchangeDay by its ID,
 * and the events associated with an ExchangeDay.
 *
 * <p>All methods in this controller are mapped to the "/api/exchange-days" URL path.</p>
 *
 * <p>Available endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/exchange-days} - Retrieves a list of all ExchangeDays.</li>
 *   <li>{@code GET /api/exchange-days/{id}} - Retrieves a specific ExchangeDay by its ID.</li>
 *   <li>{@code GET /api/exchange-days/{id}/events} - Retrieves a list of events for a specific ExchangeDay.</li>
 *   <li>{@code POST /api/exchange-days} - Creates a new ExchangeDay.</li>
 *   <li>{@code PUT /api/exchange-days/{id}} - Updates an existing ExchangeDay by its ID.</li>
 *   <li>{@code DELETE /api/exchange-days/{id}} - Deletes an existing ExchangeDay by its ID.</li>
 * </ul>
 *
 * <p>Each response is returned with an appropriate HTTP status code and the data requested or created.</p>
 */
@RestController
@RequestMapping("/api/exchange-days")
public class ExchangeDayController {

    @Autowired
    private ExchangeDayService exchangeDayService;

    /**
     * Retrieves all ExchangeDays.
     *
     * @return A list of all ExchangeDays in the form of {@link ExchangeDayResponseDTO}.
     */
    @GetMapping("")
    public Iterable<ExchangeDayResponseDTO> getAllExchangeDays() {
        return exchangeDayService.getAllExchangeDays();
    }

    /**
     * Retrieves a specific ExchangeDay by its ID.
     *
     * @param id The ID of the ExchangeDay to retrieve.
     * @return The {@link ExchangeDayResponseDTO} representing the requested ExchangeDay.
     */
    @GetMapping("/{id}")
    public ExchangeDayResponseDTO getExchangeDayById(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayById(id);
    }

    /**
     * Retrieves the events associated with a specific ExchangeDay.
     *
     * @param id The ID of the ExchangeDay whose events to retrieve.
     * @return A list of {@link EventResponseDTO} representing the events associated with the specified ExchangeDay.
     */
    @GetMapping("/{id}/events")
    public Iterable<EventResponseDTO> getExchangeDayEvents(@PathVariable Long id) {
        return exchangeDayService.getExchangeDayEvents(id);
    }

    /**
     * Creates a new ExchangeDay.
     *
     * @param requestBody The request body containing the details for the new ExchangeDay.
     * @param bindingResult Contains any validation errors that occur during the binding process.
     * @return A {@link ResponseEntity} with a status of CREATED and the saved ExchangeDay.
     */
    @PostMapping("")
    public ResponseEntity<ExchangeDayResponseDTO> createExchangeDay(@Valid @RequestBody ExchangeDayRequestDTO requestBody, BindingResult bindingResult) {
        ExchangeDayResponseDTO savedDTO = exchangeDayService.createExchangeDay(requestBody);
        URI location = URI.create("/api/exchange-days/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
    }

    /**
     * Updates an existing ExchangeDay by its ID.
     *
     * @param id The ID of the ExchangeDay to update.
     * @param requestBody The updated details of the ExchangeDay.
     * @return The updated {@link ExchangeDayResponseDTO}.
     */
    @PutMapping("/{id}")
    public ExchangeDayResponseDTO updateExchangeDay(@PathVariable Long id, @RequestBody ExchangeDayRequestDTO requestBody) {
        return exchangeDayService.updateExchangeDay(id, requestBody);
    }

    /**
     * Deletes an existing ExchangeDay by its ID.
     *
     * @param id The ID of the ExchangeDay to delete.
     * @return A {@link ResponseEntity} with a status of NO_CONTENT indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExchangeDay(@PathVariable Long id) {
        exchangeDayService.deleteExchangeDayById(id);
        return ResponseEntity.noContent().build();
    }
}
