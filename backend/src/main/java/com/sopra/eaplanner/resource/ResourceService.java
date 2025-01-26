package com.sopra.eaplanner.resource;

import com.sopra.eaplanner.locations.Location;
import com.sopra.eaplanner.locations.LocationRepository;
import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private LocationRepository locationRepository;

    public Iterable<ResourceResponse> getAllResources() {
        Iterable<ResourceItem> resources = resourceRepository.findAll();
        List<ResourceResponse> dtos = new ArrayList<>();
        for (ResourceItem resource : resources) {
            dtos.add(new ResourceResponse(resource));
        }
        return dtos;
    }

    public ResourceResponse getResourceById(Long id) {
        ResourceItem resource = resourceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resource not found"));

        return new ResourceResponse(resource);
    }

    public ResourceResponse createResource(ResourceRequest requestBody) {
        Location location = locationRepository.findById(requestBody.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        ResourceItem resourceToSave = resourceRepository.save(new ResourceItem(requestBody, location));
        resourceToSave.setAvailability(resourceToSave.getCapacity() > 0);
        return new ResourceResponse(resourceToSave);
    }

    public ResourceResponse updateResource(Long id, ResourceRequest updatedResource) {
        Location location = locationRepository.findById(updatedResource.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        ResourceItem resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        resource.setName(updatedResource.getName());
        resource.setType(Enum.valueOf(ResourceType.class, updatedResource.getType().toUpperCase()));
        resource.setDescription(updatedResource.getDescription());
        resource.setCapacity(updatedResource.getCapacity());
        resource.setLocation(location);
        resource.setAvailability(updatedResource.getAvailability());
        resourceRepository.save(resource);
        return new ResourceResponse(resource);
    }

    public ResourceResponse deleteResource(Long id) {
        ResourceItem resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        resourceRepository.deleteById(id);
        return new ResourceResponse(resource);
    }

    public Iterable<ResourceResponse> getResourcesByType(String type) {
        return StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .filter(resource -> resource.getType().toString().equalsIgnoreCase(type))
                        .map(ResourceResponse::new)
                .collect(Collectors.toList());
    }

    public Iterable<ResourceResponse> getRoomsByLocation(Long locationId) {
        return StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .filter(resource -> resource.getLocation().getId().equals(locationId) && resource.getType().equals(ResourceType.ROOM))
                .map(ResourceResponse::new)
                .collect(Collectors.toList());
    }

    public String generateCSV() {
        StringBuilder csvContent = new StringBuilder("Id,Name,Art,Beschreibung,Kapazität,Land, Postleitzahl, Stadt, Straße, Hausnummer, Verfügbarkeit\n");
        for (ResourceItem resource : (List<ResourceItem>) resourceRepository.findAll()) {
            csvContent.append(formatResourceToCSV(resource));
        }
        return csvContent.toString();
    }

    private String formatResourceToCSV(ResourceItem resource) {
        String location = String.join(",",
                escapeCSV(resource.getLocation().getCountry()),
                escapeCSV(resource.getLocation().getPostalCode()),
                escapeCSV(resource.getLocation().getCity()),
                escapeCSV(resource.getLocation().getStreet()),
                escapeCSV(resource.getLocation().getHouseNumber())
        );

        return String.join(",",
                String.valueOf(resource.getId()),
                escapeCSV(resource.getName()),
                String.valueOf(resource.getType()),
                escapeCSV(resource.getDescription()),
                String.valueOf(resource.getCapacity()),
                location,
                String.valueOf(resource.getAvailability())
        ) + "\n";
    }

    /**
     * Escapes special characters for CSV formatting.
     *
     * @param field the field to escape.
     * @return the escaped field.
     */
    private String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    public void importResourcesFromCsv(MultipartFile file) throws Exception {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = csvReader.readNext();

            if (headers == null || headers.length == 0) {
                throw new Exception("Leere oder ungültige CSV-Datei!");
            }

            List<ResourceItem> resources = new ArrayList<>();
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 9) {
                    throw new Exception("Ungültige CSV-Zeile: Zu wenig Daten");
                }
                ResourceItem resource = new ResourceItem();

                resource.setName(line[0]);
                String typeStr = line[1];
                ResourceType type;
                try {
                    type = ResourceType.valueOf(typeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new Exception("Ungültiger Ressourcentyp: " + typeStr);
                }
                resource.setType(type);

                resource.setDescription(line[2]);

                try {
                    resource.setCapacity(Integer.parseInt(line[3]));
                } catch (NumberFormatException e) {
                    throw new Exception("Ungültiger Wert für Kapazität: " + line[3]);
                }

                Location location = new Location();
                location.setCountry(line[4]);
                location.setPostalCode(line[5]);
                location.setCity(line[6]);
                location.setStreet(line[7]);
                location.setHouseNumber(line[8]);

                Location savedLocation = locationRepository.save(location);

                resource.setLocation(savedLocation);

                resources.add(resource);
            }

            resourceRepository.saveAll(resources);

        } catch (Exception e) {
            throw new Exception("Fehler beim Verarbeiten der CSV-Datei: " + e.getMessage(), e);
        }
    }

}
