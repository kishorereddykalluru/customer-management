package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.entity.Customer;
import com.customermanagement.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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

    /**
     * Returns all customers information present in db
     * @return
     */
    @PerfProfiler
    public Optional<List<CustomerDetails>> getAllCustomers(){
        //first method to retrieve employees and store in employee details
        List<CustomerDetails> customers = createCustomers(customerRepository.findAllCustomers());
        //same way complete in single line
        customerRepository.findAllCustomers().stream().map(customer ->
                        CustomerDetails.builder().id(customer.getId())
                                .customerName(customer.getCustomerName())
                                .contactName(customer.getContactName())
                                .city(customer.getCity())
                                .country(customer.getCountry())
                                .build()
        ).collect(Collectors.toList());
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
     * @return List of CustomerDetails
     */
    @PerfProfiler
    public Optional<List<CustomerDetails>> getCustomersByIds(List<Long> customerIds) {

        List<List<Long>> partition = ListUtils.partition(customerIds, partitionSize);
        List<CompletableFuture<List<Customer>>> collect = partition.stream().map(ids ->
                customerAggregationService.findCustomerByIds(ids).exceptionally(e -> null)
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

    /**
     *
     * gets customer details by single id
     *
     * @param id
     * @return customer details
     */
    @Cacheable("customer-management")
    public Optional<CustomerDetails> getCustomersById(Long id) {
        log.info("customer id " + id);
        Customer customer = customerRepository.findById(id).orElseGet(null);

        return ObjectUtils.isEmpty(customer) ? Optional.empty() : Optional.of(CustomerDetails.builder()
                .customerName(customer.getCustomerName())
                .contactName(customer.getContactName())
                .id(customer.getId())
                .country(customer.getCountry())
                .city(customer.getCity())
                .build());

    }

    /**
     * Add customer to Data base
     *
     * @param customer
     * @return
     */
    public String addCustomer(Customer customer) {
        Customer save = customerRepository.save(customer);
        if(Objects.nonNull(save))
            return "customer saved to db successfully " + customer.getId();
        else
            return "Failed to save customer details";
    }

    /**
     * Updated customer in db
     *
     * @param customer
     * @return
     */
    public String updateCustomer(Customer customer) {
        Customer save = customerRepository.save(customer);

        return Objects.nonNull(customer) ? "Customer updated successfully "+ customer.getId(): "Failed to update Customer";
    }

    /**
     * delete customer based on id from db
     *
     * @param id
     * @return
     */
    public String deleteCustomer(Long id) {
        Optional<Customer> byId = customerRepository.findById(id);
        if(byId.isPresent()){
            customerRepository.deleteById(id);
            return "Customer deleted successful with id "+id;
        } else {
            return "Deletion unsuccessful customer not found";
        }
    }
}
