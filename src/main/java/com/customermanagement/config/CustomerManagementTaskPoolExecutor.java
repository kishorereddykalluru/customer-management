package com.customermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class CustomerManagementTaskPoolExecutor {

    @Value("${threadPool.customerManagement.corePoolSize: 2}")
    private int customerManagementCorePoolSize;

    @Value("${threadPool.customerManagement.maxPoolSize: 200}")
    private int customerManagementMaxPoolSize;

    @Value("${threadPool.customerManagement.keepAliveSeconds: 300}")
    private int customerManagementKeepAliveSeconds;

    @Bean
    public Executor customerManagementThreadPoolExecutor(){
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setThreadNamePrefix("customer-svc-");
        threadPoolExecutor.setCorePoolSize(customerManagementCorePoolSize);
        threadPoolExecutor.setMaxPoolSize(customerManagementMaxPoolSize);
        threadPoolExecutor.setKeepAliveSeconds(customerManagementKeepAliveSeconds);

        return threadPoolExecutor;
    }
}
