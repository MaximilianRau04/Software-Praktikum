package com.sopra.eaplanner.locations;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final Map<String, List<String>> countriesAndCities;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

        countriesAndCities = new HashMap<>();
        countriesAndCities.put("Deutschland", List.of("Berlin", "München", "Hamburg", "Köln", "Stuttgart","Nürnberg", "Hannover", "Trier", "Passau", "Tutzing"));
        countriesAndCities.put("USA", List.of("New York", "Los Angeles", "Chicago"));
        countriesAndCities.put("Frankreich", List.of("Paris", "Marseille", "Lyon"));
        countriesAndCities.put("Spanien", List.of("Madrid", "Barcelona", "Sevilla"));
        countriesAndCities.put("Estland", List.of("Tallinn"));
        countriesAndCities.put("Schweden", List.of("Stockholm"));
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location updateLocation(Long id, Location updatedLocation) {
        return locationRepository.findById(id)
                .map(location -> {
                    location.setCity(updatedLocation.getCity());
                    location.setPostalCode(updatedLocation.getPostalCode());
                    location.setStreet(updatedLocation.getStreet());
                    location.setHouseNumber(updatedLocation.getHouseNumber());
                    location.setCountry(updatedLocation.getCountry());
                    return locationRepository.save(location);
                })
                .orElseThrow(() -> new RuntimeException("Location not found with id " + id));
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    public List<String> getAllCountries() {
        return List.copyOf(countriesAndCities.keySet());
    }

    public List<String> getCitiesByCountry(String country) {
        return countriesAndCities.getOrDefault(country, List.of());
    }

    public void addLocation(Location location) {
        countriesAndCities.computeIfAbsent(location.getCountry(), k -> new ArrayList<>())
                .add(location.getCity());
    }
}
