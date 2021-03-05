package com.customermanagement.controller;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @GetMapping(value = "/customers/getAll")
    public List<CustomerDetails> getAllCustomers(){
        return customerService.getAllCustomers().orElseThrow(() ->new RuntimeException("Customers not found"));
    }
}
