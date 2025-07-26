package com.cab_app.cab.controller;

import com.cab_app.cab.dao.entity.Customer;
import com.cab_app.cab.dao.entity.Trip;
import com.cab_app.cab.dto.TripRequest;
import com.cab_app.cab.service.CustomerService;
import com.cab_app.cab.service.OtpService;
import com.cab_app.cab.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TripService tripService;

    @Autowired
    private OtpService otpService;

    // -------------------------
    // Register a new customer and send OTP
    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody Customer customer) {
        try {
            Customer saved = customerService.registerCustomer(customer);
            otpService.generateOtp(saved.getPhoneNo()); // In real app, send via SMS
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // -------------------------
    // Login using phone and OTP
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String phoneNo, @RequestParam String otp) {
        try {
            boolean isValid = otpService.verifyOtp(phoneNo, otp);
            return isValid ?
                    ResponseEntity.ok("Login successful") :
                    ResponseEntity.status(401).body("Invalid OTP");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    // -------------------------
    // Get trip history for a customer
    @GetMapping("/{id}/trips")
    public ResponseEntity<?> getTripHistory(@PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            if (customer == null) {
                return ResponseEntity.status(404).body("Customer not found");
            }
            List<Trip> trips = tripService.getTripsByCustomerId(id);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    // -------------------------
    // Book a ride (with pickup & drop locations)
    @PostMapping("/{id}/book")
    public ResponseEntity<?> bookTrip(@PathVariable Long id, @RequestBody TripRequest tripRequest) {
        try {
            Trip trip = tripService.bookTrip(id, tripRequest);
            return ResponseEntity.ok(trip);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Could not book trip");
        }
    }

    // -------------------------
    // Verify Trip OTP to start the ride
    @PostMapping("/{id}/verify-otp")
    public ResponseEntity<String> verifyTripOtp(@RequestParam String tripOtp, @PathVariable Long id) {
        try {
            boolean verified = tripService.verifyTripOtp(id, tripOtp);
            return verified ?
                    ResponseEntity.ok("OTP Verified, trip can start.") :
                    ResponseEntity.status(400).body("Invalid OTP");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Verification failed");
        }
    }

    // -------------------------
    // Mark fare as paid
    @PostMapping("/{id}/pay")
    public ResponseEntity<String> payFare(@RequestParam UUID tripId) {
        try {
            boolean paid = tripService.markTripAsPaid(tripId);
            return paid ?
                    ResponseEntity.ok("Payment Successful") :
                    ResponseEntity.badRequest().body("Trip not found or already paid");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Payment processing failed");
        }
    }
}
