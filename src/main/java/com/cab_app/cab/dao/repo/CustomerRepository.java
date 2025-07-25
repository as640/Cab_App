package com.cab_app.cab.dao.repo;

import com.cab_app.cab.dao.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find by phone number (returns Optional to handle "not found" cases safely)
    Optional<Customer> findByPhoneNo(String phoneNo);

    // Find by publicId (UUID field)
    Optional<Customer> findByPublicId(java.util.UUID publicId);
}
