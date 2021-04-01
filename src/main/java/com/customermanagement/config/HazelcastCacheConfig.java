package com.customermanagement.config;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.entity.Customer;
import com.customermanagement.service.CustomerService;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HazelcastCacheConfig {

    @Bean
    IMap<Long, Customer> customerIMap(HazelcastInstance hazelcastInstance){
        return hazelcastInstance.getMap(customerCache().getName());
    }

    @Bean
    public Map<String, MapConfig> hzCaches(){
        Map<String, MapConfig> mapConfigs = new HashMap<>();
        mapConfigs.put(customerCache().getName(), customerCache());
        return mapConfigs;
    }

    /**
     *
     * Key: customerId
     * Value: Customer
     */
    private MapConfig customerCache() {
        return new MapConfig().setName(CustomerService.CUSTOMER_CACHE)
                .setBackupCount(1)
                .setStatisticsEnabled(true)
                .setEvictionConfig(new EvictionConfig()
                        .setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE).setEvictionPolicy(EvictionPolicy.LFU))
                .setTimeToLiveSeconds(86400);
    }
}
