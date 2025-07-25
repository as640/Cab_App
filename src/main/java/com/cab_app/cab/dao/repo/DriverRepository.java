package com.cab_app.cab.dao.repo;

import com.cab_app.cab.dao.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Custom query to find by phone number
    Optional<Driver> findByPhoneNo(String phoneNo);

    // Optional: Find by public UUID
    Optional<Driver> findByPublicId(java.util.UUID publicId);
}
