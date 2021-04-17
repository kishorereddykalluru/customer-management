package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.exception.NotFoundException;
import com.customermanagement.persistence.entity.Customer;
import com.customermanagement.persistence.CustomerRepository;
import com.customermanagement.utils.AsyncUtil;
import com.customermanagement.utils.CompletableFutureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    public static final String CUSTOMER_CACHE = "customer-management";

    @Value("${partition-ids.size}")
    private int partitionSize;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAggregationService customerAggregationService;

    @Autowired
    private AsyncUtil asyncUtil;

    @Autowired
    private CompletableFutureUtil completableFutureUtil;

    /**
     * Returns all customers information present in db
     * @return list of customer details
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
     * @param customerIds ids to be specified
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
     * @param id customer id to be specified
     * @return customer details
     */
    @Cacheable(CUSTOMER_CACHE)
    public Optional<CustomerDetails> getCustomersById(Long id) {
        log.info("customer id " + id);
        log.info("should be executed when method is executed and request goes to repository");
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found with id " +id));

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
     * @param customer customer to be saved in database
     * @return success or failure message
     */
    @CachePut(cacheNames = CUSTOMER_CACHE, key = "#customer.id")
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);

    }

    /**
     * Updated customer in db
     *
     * @param customer customer to be saved in database
     * @return success or failure message
     */
    @CachePut(cacheNames = CUSTOMER_CACHE, key = "#customer.id")
    public Customer updateCustomer(Customer customer) {
       return customerRepository.save(customer);
    }

    /**
     * delete customer based on id from db
     *
     * @param id customer id to be specified
     * @return success or failure message
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

    /**
     * run some parallel cases in util package
     */
    @PerfProfiler
    public void completeableFutureTesting(String condition) throws ExecutionException, InterruptedException {
        String s = null;
        String s1 = null;
        String s2 = null;
        String s3 = null;

        if("one".equals(condition)){
            CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> completableFutureUtil.method1());
            s = stringCompletableFuture.get();
        }
        if("two".equals(condition)) {
            CompletableFuture<String> stringCompletableFuture1 = CompletableFuture.supplyAsync(() -> completableFutureUtil.method2());
            s1 = stringCompletableFuture1.get();
        }
        if("three".equals(condition)) {
            CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(() -> completableFutureUtil.method3());
            s2 = stringCompletableFuture2.get();
        }
        if("four".equals(condition)) {
            CompletableFuture<String> stringCompletableFuture3 = CompletableFuture.supplyAsync(() -> completableFutureUtil.method4());
            s3 = stringCompletableFuture3.get();
        }



        System.out.println("s = " + s);
        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);
        System.out.println("s3 = " + s3);

    }


    @PerfProfiler
    public void asyncTesting(String condition) throws ExecutionException, InterruptedException {
        CompletableFuture<String> s = asyncUtil.method1();
        CompletableFuture<String> s1 = asyncUtil.method2();
        CompletableFuture<String> s2 = asyncUtil.method3();
        CompletableFuture<String> s3 = asyncUtil.method4();


        System.out.println("s = " + s.get());
        System.out.println("s1 = " + s1.get());
        System.out.println("s2 = " + s2.get());
        System.out.println("s3 = " + s3.get());

    }
}
