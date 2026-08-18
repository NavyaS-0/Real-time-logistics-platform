package com.logistics.trackingservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TrackingLocationRepository repository;

    public DataSeeder(TrackingLocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return;
        }

        // Shipment 1
        add(1L, "Chennai", "PICKED_UP", 5);
        add(1L, "Bangalore", "IN_TRANSIT", 4);
        add(1L, "Hyderabad", "IN_TRANSIT", 3);
        add(1L, "Mumbai", "IN_TRANSIT", 2);
        add(1L, "Delhi", "OUT_FOR_DELIVERY", 1);

        // Shipment 2
        add(2L, "Bangalore", "PICKED_UP", 5);
        add(2L, "Pune", "IN_TRANSIT", 4);
        add(2L, "Mumbai", "IN_TRANSIT", 2);

        // Shipment 3
        add(3L, "Hyderabad", "PICKED_UP", 5);
        add(3L, "Bangalore", "IN_TRANSIT", 3);
        add(3L, "Chennai", "IN_TRANSIT", 1);

        // Shipment 4
        add(4L, "Mumbai", "PICKED_UP", 5);
        add(4L, "Pune", "IN_TRANSIT", 4);
        add(4L, "Bangalore", "DELIVERED", 1);

        // Shipment 5
        add(5L, "Delhi", "PICKED_UP", 5);
        add(5L, "Agra", "IN_TRANSIT", 3);
        add(5L, "Hyderabad", "IN_TRANSIT", 1);

        // Shipment 6
        add(6L, "Chennai", "PICKED_UP", 5);
        add(6L, "Bangalore", "IN_TRANSIT", 3);
        add(6L, "Bangalore", "DELIVERED", 1);

        // Shipment 7
        add(7L, "Pune", "PICKED_UP", 5);
        add(7L, "Jaipur", "IN_TRANSIT", 3);
        add(7L, "Delhi", "IN_TRANSIT", 1);

        // Shipment 8
        add(8L, "Kolkata", "PICKED_UP", 5);
        add(8L, "Bhubaneswar", "IN_TRANSIT", 3);
        add(8L, "Mumbai", "OUT_FOR_DELIVERY", 1);

        // Shipment 9
        add(9L, "Bangalore", "PICKED_UP", 5);
        add(9L, "Hyderabad", "IN_TRANSIT", 3);
        add(9L, "Delhi", "IN_TRANSIT", 1);

        // Shipment 10
        add(10L, "Hyderabad", "PICKED_UP", 5);
        add(10L, "Nagpur", "IN_TRANSIT", 3);
        add(10L, "Mumbai", "DELIVERED", 1);

        System.out.println("Sample tracking data inserted.");
    }

    private void add(
            Long shipmentId,
            String location,
            String status,
            int hoursAgo) {

        TrackingLocation tracking = new TrackingLocation();

        tracking.setShipmentId(shipmentId);
        tracking.setLocation(location);
        tracking.setStatus(status);
        tracking.setTimestamp(
                Instant.now().minusSeconds(hoursAgo * 60L * 60L)
        );

        repository.save(tracking);
    }
}