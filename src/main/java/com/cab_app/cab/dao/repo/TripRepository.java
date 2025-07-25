package com.cab_app.cab.dao.repo;

import com.cab_app.cab.common.constant.TripStatus;
import com.cab_app.cab.dao.entity.Trip;
import com.cab_app.cab.common.constant.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByPublicId(UUID publicId);

    List<Trip> findByCustomerCustomerId(Long customerId);

    List<Trip> findByDriverDriverId(Long driverId);

    List<Trip> findByStatus(TripStatus status);

    List<Trip> findByCustomerPublicId(UUID customerPublicId);
}
