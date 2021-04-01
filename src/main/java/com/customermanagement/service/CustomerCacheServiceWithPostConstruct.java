package com.customermanagement.service;

import com.customermanagement.aspect.PerfProfiler;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.CustomerRepository;
import com.customermanagement.persistence.entity.Customer;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerCacheServiceWithPostConstruct {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    @Qualifier("customerIMap")
    IMap<Long, Customer> customerIMap;

    //@PostConstruct // for post construct
    @EventListener // when loaded using event listener
    @PerfProfiler
    public void initialize(ContextRefreshedEvent event){
        log.debug("customers map size is, {}",customerIMap.size());
        if(customerIMap.size() == 0){
            log.debug("customer cache size is 0. Loading caches.....");
            List<Customer> allCustomers = repository.findAllCustomers();
            Map<Long, Customer> collect = allCustomers.stream().collect(Collectors.toMap(Customer::getId, c -> c));
            customerIMap.putAll(collect);
            log.debug("customer caches loaded with size ...., {}",customerIMap.size());
        } else {
            log.debug("customer cache is already populated in Customer count. Not refreshing from DB");
        }
    }
}
