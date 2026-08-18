package com.logistics.shipmentservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ShipmentRepository shipmentRepository;

    public DataSeeder(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public void run(String... args) {

        if (shipmentRepository.count() > 0) {
            return;
        }

        createShipment("TRK001", "Chennai", "Delhi", "OUT_FOR_DELIVERY");
        createShipment("TRK002", "Bangalore", "Mumbai", "IN_TRANSIT");
        createShipment("TRK003", "Hyderabad", "Chennai", "IN_TRANSIT");
        createShipment("TRK004", "Mumbai", "Bangalore", "DELIVERED");
        createShipment("TRK005", "Delhi", "Hyderabad", "IN_TRANSIT");
        createShipment("TRK006", "Chennai", "Bangalore", "DELIVERED");
        createShipment("TRK007", "Pune", "Delhi", "IN_TRANSIT");
        createShipment("TRK008", "Kolkata", "Mumbai", "OUT_FOR_DELIVERY");
        createShipment("TRK009", "Bangalore", "Delhi", "IN_TRANSIT");
        createShipment("TRK010", "Hyderabad", "Mumbai", "DELIVERED");

        System.out.println("10 sample shipments inserted.");
    }

    private void createShipment(
            String trackingNumber,
            String origin,
            String destination,
            String status) {

        Shipment shipment = new Shipment();

        shipment.setTrackingNumber(trackingNumber);
        shipment.setOrigin(origin);
        shipment.setDestination(destination);
        shipment.setStatus(status);

        shipmentRepository.save(shipment);
    }
}