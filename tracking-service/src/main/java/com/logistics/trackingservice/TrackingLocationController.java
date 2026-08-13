package com.logistics.trackingservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "http://localhost:3000")
public class TrackingLocationController {

    private final TrackingLocationRepository trackingLocationRepository;
    private final KafkaProducer kafkaProducer;

    public TrackingLocationController(
            TrackingLocationRepository trackingLocationRepository,
            KafkaProducer kafkaProducer) {

        this.trackingLocationRepository = trackingLocationRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public TrackingLocation addLocation(
            @RequestBody TrackingRequest request) {

        TrackingLocation trackingLocation = new TrackingLocation();

        trackingLocation.setShipmentId(request.shipmentId());
        trackingLocation.setLocation(request.location());
        trackingLocation.setStatus(request.status());
        trackingLocation.setTimestamp(Instant.now());

        TrackingLocation savedLocation =
                trackingLocationRepository.save(trackingLocation);

        String message = "Shipment " + request.shipmentId()
                + " moved to " + request.location()
                + " with status " + request.status();

        kafkaProducer.sendTrackingUpdate(message);

        return savedLocation;
    }

    @GetMapping("/{shipmentId}")
    public List<TrackingLocation> getTrackingHistory(
            @PathVariable Long shipmentId) {

        return trackingLocationRepository
                .findByShipmentIdOrderByTimestampAsc(shipmentId);
    }

    @GetMapping("/{shipmentId}/latest")
    public ResponseEntity<TrackingLocation> getLatestLocation(
            @PathVariable Long shipmentId) {

        List<TrackingLocation> history =
                trackingLocationRepository
                        .findByShipmentIdOrderByTimestampAsc(shipmentId);

        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(history.get(history.size() - 1));
    }

    public record TrackingRequest(
            Long shipmentId,
            String location,
            String status
    ) {
    }
}