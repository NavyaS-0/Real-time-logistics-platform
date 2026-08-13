package com.logistics.shipmentservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final CustomerRepository customerRepository;
    private final TrackingEventRepository trackingEventRepository;

    public ShipmentController(
            ShipmentRepository shipmentRepository,
            CustomerRepository customerRepository,
            TrackingEventRepository trackingEventRepository) {

        this.shipmentRepository = shipmentRepository;
        this.customerRepository = customerRepository;
        this.trackingEventRepository = trackingEventRepository;
    }

    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return shipmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/customer")
    public ResponseEntity<Shipment> assignCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequest request) {

        return shipmentRepository.findById(id)
                .map(shipment -> customerRepository.findById(request.customerId())
                        .map(customer -> {
                            shipment.setCustomer(customer);
                            return ResponseEntity.ok(shipmentRepository.save(shipment));
                        })
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Shipment> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return shipmentRepository.findById(id)
                .map(shipment -> {
                    shipment.setStatus(status);
                    return ResponseEntity.ok(shipmentRepository.save(shipment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/tracking")
    public ResponseEntity<TrackingEvent> addTrackingEvent(
            @PathVariable Long id,
            @RequestBody TrackingRequest request) {

        return shipmentRepository.findById(id)
                .map(shipment -> {

                    TrackingEvent event = new TrackingEvent();
                    event.setLocation(request.location());
                    event.setStatus(request.status());
                    event.setTimestamp(Instant.now());
                    event.setShipment(shipment);

                    return ResponseEntity.ok(
                            trackingEventRepository.save(event)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<List<TrackingEvent>> getTrackingHistory(
            @PathVariable Long id) {

        if (!shipmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                trackingEventRepository
                        .findByShipmentIdOrderByTimestampAsc(id)
        );
    }

    public record CustomerRequest(Long customerId) {
    }

    public record TrackingRequest(
            String location,
            String status
    ) {
    }
}
