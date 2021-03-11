package com.customermanagement.controller;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Endpoint to return list of all customers
     *
     * @return CustomerDetails List
     */
    @GetMapping(value = "/customers/getAll")
    public List<CustomerDetails> getAllCustomers(){
        return customerService.getAllCustomers().orElseThrow(() ->new RuntimeException("Customers not found"));
    }

    /**
     * Endpoint takes list of ids as input and return customer details of provided ids
     *
     * @param ids
     * @return CustomerDetails list
     */

    @Operation(operationId = "findByCustomerId", description = "Customers Management", summary = "Find Customers by ids.",
                responses =  {
                        @ApiResponse(responseCode = "200", description = "successful response received", content = @Content(schema = @Schema(implementation = CustomerDetails.class))),
                        @ApiResponse(responseCode = "400", description = "Not Found error"),
                        @ApiResponse(responseCode = "406", description = "Not Acceptable Error"),
                        @ApiResponse(responseCode = "500", description = "Internale Server error")

                })
    @GetMapping(value = "/customers")
    public List<CustomerDetails> findByCustomerId(
            @Parameter(name = "id", in = ParameterIn.QUERY, description = "Customer Id")
            @RequestParam("id") List<Long> ids){
        return customerService.getCustomersByIds(ids).orElseThrow(() ->new RuntimeException("Customer not found"));
    }
}
