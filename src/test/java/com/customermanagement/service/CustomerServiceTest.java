package com.customermanagement.service;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.persistence.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerAggregationService customerAggregationService;

    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(customerService, "partitionSize", 1);
    }

    @Test
    void testGetAllCustomers() {

    }

    @Test
    void tesGetCustomersByIds() {
        Customer customer = Customer.builder().id(1l).customerName("Kishore").build();
        when(customerAggregationService.findCustomerByIds(anyList())).thenReturn(CompletableFuture.completedFuture(List.of(customer)));
        Optional<List<CustomerDetails>> customersByIds = customerService.getCustomersByIds(List.of(1l, 2l, 3l, 4l));

        assertTrue(customersByIds.isPresent());
        assertEquals("Kishore", customersByIds.get().get(0).getCustomerName());
    }
}