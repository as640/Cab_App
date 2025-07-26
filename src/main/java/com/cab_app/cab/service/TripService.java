package com.cab_app.cab.service;

import com.cab_app.cab.common.constant.TripStatus;
import com.cab_app.cab.dao.entity.*;
import com.cab_app.cab.dao.repo.*;
import com.cab_app.cab.dto.TripRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OtpService otpService;

    // -----------------------
    // Book Trip
    public Trip bookTrip(Long customerId, TripRequest req) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 1. Create and save pickup/drop locations
        Location pickup = new Location("Pickup", req.getPickupLat(), req.getPickupLng());
        Location drop = new Location(req.getDropCity(), req.getDropLat(), req.getDropLng());
        locationRepository.saveAll(List.of(pickup, drop));

        // 2. Find nearest available driver (basic simulation)
        Optional<Driver> optionalDriver = driverRepository.findAll().stream()
                .filter(Driver::isAvailable)
                .findFirst(); // Simplified: In real use lat/lng filtering + Haversine

        if (optionalDriver.isEmpty()) {
            throw new RuntimeException("No available drivers nearby");
        }

        Driver driver = optionalDriver.get();
        driver.setAvailable(false);
        driverRepository.save(driver);

        // 3. Calculate estimated fare (simple)
        double estimatedDistance = calculateDistance(
                req.getPickupLat(), req.getPickupLng(),
                req.getDropLat(), req.getDropLng()
        );
        double estimatedFare = 50 + 10 * estimatedDistance; // base + per km rate

        // 4. Create Trip
        Trip trip = new Trip();
        trip.setPublicId(UUID.randomUUID());
        trip.setCustomer(customer);
        trip.setDriver(driver);
        trip.setStartLocation(pickup);
        trip.setEndLocation(drop);
        trip.setStatus(TripStatus.REQUESTED);
        trip.setOtp(generateOtp());
        trip.setOtpVerified(false);
        trip.setFare(estimatedFare);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());

        return tripRepository.save(trip);
    }

    // -----------------------
    // Get Trips by Customer
    public List<Trip> getTripsByCustomerId(Long customerId) {
        return tripRepository.findByCustomerCustomerId(customerId);
    }

    // -----------------------
    // Verify OTP
    public boolean verifyTripOtp(Long customerId, String otp) {
        Optional<Trip> tripOpt = tripRepository.findByCustomerCustomerId(customerId).stream()
                .filter(t -> !t.isOtpVerified() && t.getStatus() == TripStatus.REQUESTED)
                .findFirst();

        if (tripOpt.isPresent()) {
            Trip trip = tripOpt.get();
            if (trip.getOtp().equals(otp)) {
                trip.setOtpVerified(true);
                trip.setStatus(TripStatus.ONGOING);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);
                return true;
            }
        }
        return false;
    }

    // -----------------------
    // Mark Trip as Paid
    public boolean markTripAsPaid(UUID tripId) {
        Optional<Trip> opt = tripRepository.findByPublicId(tripId);
        if (opt.isPresent()) {
            Trip trip = opt.get();
            if (trip.getStatus() == TripStatus.ONGOING || trip.getStatus() == TripStatus.COMPLETED) {
                trip.setStatus(TripStatus.COMPLETED);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);

                // Free the driver
                Driver driver = trip.getDriver();
                driver.setAvailable(true);
                driverRepository.save(driver);

                return true;
            }
        }
        return false;
    }

    // -----------------------
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Dummy distance: Always 3 km
        return 3.0;

        // TODO: Integrate Google Distance Matrix API
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(9000) + 1000); // 4-digit
    }
}
