package com.logistics.trackingservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingLocationRepository
        extends JpaRepository<TrackingLocation, Long> {

    List<TrackingLocation> findByShipmentIdOrderByTimestampAsc(Long shipmentId);
}