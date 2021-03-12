package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.Customer;
import com.customermanagement.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    @Value("${partiton-ids.size}")
    private int partitionSize;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAggregationService customerAggregationService;

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

    /**
     * Returns List of customer details based on ids passed
     *
     * @param customerIds
     * @return
     */
    @PerfProfiler
    public Optional<List<CustomerDetails>> getCustomersByIds(List<Long> customerIds) {

        List<List<Long>> partition = ListUtils.partition(customerIds, partitionSize);
        List<CompletableFuture<List<Customer>>> collect = partition.stream().map(ids ->
                customerAggregationService.findCustomerById(ids).exceptionally(e -> null)
        ).collect(Collectors.toList());

        List<CustomerDetails> customers = createCustomers(customerDetailsResponse(collect));

        if(!CollectionUtils.isEmpty(customers)){
            log.info("Customer count {}", customers.size());
        }
        return CollectionUtils.isEmpty(customers)? Optional.empty(): Optional.of(customers);
    }

    private List<Customer> customerDetailsResponse(List<CompletableFuture<List<Customer>>> collect) {
        List<Customer> cusomers = collect.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return cusomers;
    }
}
