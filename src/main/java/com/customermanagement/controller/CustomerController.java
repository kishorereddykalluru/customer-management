package com.customermanagement.controller;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Customer Management")
public class CustomerController {



    @Autowired
    private CustomerService customerService;

    /**
     * Endpoint to return list of all customers
     *
     * @return CustomerDetails List
     */
    @GetMapping(value = "${customer-management.getAll}")
    @Operation(operationId = "getAllCustomers", description = "Customers Management", summary = "Get All Customers",
            responses =  {
                    @ApiResponse(responseCode = "200", description = "successful response received", content = @Content(schema = @Schema(implementation = CustomerDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Not Found error"),
                    @ApiResponse(responseCode = "406", description = "Not Acceptable Error"),
                    @ApiResponse(responseCode = "500", description = "Internale Server error")

            })
    public List<CustomerDetails> getAllCustomers(){
        return customerService.getAllCustomers().orElseThrow(() ->new NotFoundException("Customers not found"));
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
    @GetMapping(value = "${customer-management.findById}")
    public List<CustomerDetails> findByCustomerId(
            @Parameter(name = "id", in = ParameterIn.QUERY, description = "Customer Id")
            @RequestParam("id") List<Long> ids){
        return customerService.getCustomersByIds(ids).orElseThrow(() ->new RuntimeException("Customer not found"));
    }
}
