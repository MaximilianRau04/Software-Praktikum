package com.sopra.eaplanner.resource;

import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @GetMapping
    public Iterable<ResourceResponse> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/type/{type}")
    public Iterable<ResourceResponse> getResourcesByType(@PathVariable String type) {
        return resourceService.getResourcesByType(type);
    }

    @GetMapping("/location/{locationId}")
    public Iterable<ResourceResponse> getRoomsByLocation(@PathVariable Long locationId) {
        return resourceService.getRoomsByLocation(locationId);
    }

    @GetMapping("/{id}")
    public ResourceResponse getResource(@PathVariable Long id) {
        return resourceService.getResourceById(id);
    }

    @GetMapping("/csv-downloads")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getResourceCSV() {
        String csvContent = resourceService.generateCSV();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resources.csv");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    @PostMapping("/csv-import")
    public ResponseEntity<String> importResourcesFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Keine Datei hochgeladen.");
        }

        try {
            resourceService.importResourcesFromCsv(file);
            return ResponseEntity.ok("CSV-Daten erfolgreich importiert.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Verarbeiten der CSV-Datei: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest requestBody) {
        ResourceResponse savedDTO = resourceService.createResource(requestBody);
        URI location = URI.create("/api/resources/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
    }

    @PutMapping("/{id}")
    public ResourceResponse updateResource(@PathVariable Long id, @RequestBody ResourceRequest resource) {
        return resourceService.updateResource(id, resource);
    }

    @DeleteMapping("/{id}")
    public ResourceResponse deleteResource(@PathVariable Long id) {
        return resourceService.deleteResource(id);
    }
}