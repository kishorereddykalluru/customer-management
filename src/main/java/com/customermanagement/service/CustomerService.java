package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.Customer;
import com.customermanagement.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @PerfProfiler
    public Optional<List<CustomerDetails>> getAllCustomers(){
        List<CustomerDetails> customers = createCustomers(customerRepository.findAllCustomers());
        return CollectionUtils.isEmpty(customers) ? Optional.empty() :  Optional.of(customers);
    }

    private List<CustomerDetails> createCustomers(List<Customer> all) {
        return all.stream().map(customer ->
                CustomerDetails.builder().id(customer.getId())
                .customerName(customer.getCustomerName())
                .contactName(customer.getContactName())
                .city(customer.getCity())
                .country(customer.getCountry())
                .build()
                ).collect(Collectors.toList());
    }
}
