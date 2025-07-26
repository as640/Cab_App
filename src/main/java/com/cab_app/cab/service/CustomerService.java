package com.cab_app.cab.service;

import com.cab_app.cab.dao.entity.Customer;
import com.cab_app.cab.dao.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Register and save a new customer
    public Customer registerCustomer(Customer customer) {
        // You can add phone number duplication check here if needed
        return customerRepository.save(customer);
    }

    // ✅ FIXED: Get customer by ID and return null if not found
    public Customer getCustomerById(Long id) {
        try {
            return customerRepository.findById(id).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get customer by phone number
    // Get customer by phone number
    public Customer getCustomerByPhoneNo(String phoneNo) {
        try {
            return customerRepository.findByPhoneNo(phoneNo).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
