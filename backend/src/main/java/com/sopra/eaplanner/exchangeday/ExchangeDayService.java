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

/**
 * Service class responsible for handling business logic related to ExchangeDay entities.
 * Provides methods for creating, retrieving, updating, and deleting ExchangeDay records.
 *
 * <p>Interacts with the {@link ExchangeDayRepository} for data persistence and the {@link LocationRepository}
 * to retrieve locations associated with the ExchangeDays.</p>
 */
@Service
public class ExchangeDayService {
    @Autowired
    private ExchangeDayRepository exchangeDayRepository;
    @Autowired
    private LocationRepository locationRepository;

    /**
     * Creates a new ExchangeDay based on the provided request data.
     *
     * @param requestBody The request data containing the details for the new ExchangeDay.
     * @return A {@link ExchangeDayResponseDTO} representing the newly created ExchangeDay.
     * @throws EntityNotFoundException If the Location associated with the ExchangeDay cannot be found.
     */
    public ExchangeDayResponseDTO createExchangeDay(ExchangeDayRequestDTO requestBody) {
        System.out.println("ID: "+ requestBody.getLocationId());
        Location location = locationRepository.findById(requestBody.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        ExchangeDay exchangeDayToSave = new ExchangeDay(requestBody, location);
        exchangeDayRepository.save(exchangeDayToSave);
        return new ExchangeDayResponseDTO(exchangeDayToSave);
    }

    /**
     * Retrieves all ExchangeDays from the repository.
     *
     * @return A list of {@link ExchangeDayResponseDTO} representing all the ExchangeDays.
     */
    public Iterable<ExchangeDayResponseDTO> getAllExchangeDays() {
        Iterable<ExchangeDay> exchangeDays = exchangeDayRepository.findAll();
        List<ExchangeDayResponseDTO> dtos = new ArrayList<>();
        for (ExchangeDay exchangeDay : exchangeDays) {
            dtos.add(new ExchangeDayResponseDTO(exchangeDay));
        }
        return dtos;
    }

    /**
     * Retrieves the events associated with a specific ExchangeDay.
     *
     * @param id The ID of the ExchangeDay whose events are to be retrieved.
     * @return A list of {@link EventResponseDTO} representing the events of the specified ExchangeDay.
     * @throws EntityNotFoundException If the ExchangeDay with the given ID cannot be found.
     */
    public Iterable<EventResponseDTO> getExchangeDayEvents(Long id) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Exchange day not found"));

        return exchangeDay.getEvents()
                .stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific ExchangeDay by its ID.
     *
     * @param id The ID of the ExchangeDay to retrieve.
     * @return A {@link ExchangeDayResponseDTO} representing the requested ExchangeDay.
     * @throws EntityNotFoundException If the ExchangeDay with the given ID cannot be found.
     */
    public ExchangeDayResponseDTO getExchangeDayById(Long id) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Exchange day not found"));

        return new ExchangeDayResponseDTO(exchangeDay);
    }

    /**
     * Updates an existing ExchangeDay with the provided details.
     *
     * @param id The ID of the ExchangeDay to update.
     * @param requestBody The updated details for the ExchangeDay.
     * @return A {@link ExchangeDayResponseDTO} representing the updated ExchangeDay.
     * @throws EntityNotFoundException If the ExchangeDay or the associated Location cannot be found.
     */
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

    /**
     * Deletes an existing ExchangeDay by its ID.
     *
     * @param id The ID of the ExchangeDay to delete.
     */
    public void deleteExchangeDayById(Long id) {
        exchangeDayRepository.deleteById(id);
    }
}
