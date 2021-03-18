package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.persistence.entity.Customer;
import com.customermanagement.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CustomerAggregationService {

    @Autowired
    private CustomerRepository customerRepository;


    @Async("customerManagementThreadPoolExecutor")
    @PerfProfiler
    public CompletableFuture<List<Customer>> findCustomerByIds(List<Long> ids) {
        log.info("Customer Ids Size {}" + ids.size());
        List<Customer> customers = customerRepository.findCustomerById(ids);
        return CompletableFuture.completedFuture(customers);
    }
}
