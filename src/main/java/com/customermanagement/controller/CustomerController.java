package com.customermanagement.controller;

import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.exception.NotFoundException;
import com.customermanagement.exception.ServiceException;
import com.customermanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@Tag(name = "Customer Management")
public class CustomerController implements ErrorController {

    private static final String ERROR_URL = "${server.error.path:${error.path:/error}}";

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
            @Parameter(name = "ids", in = ParameterIn.QUERY, description = "Customer Id")
            @RequestParam("ids") List<Long> ids){
        return customerService.getCustomersByIds(ids).orElseThrow(() ->new NotFoundException("Customer not found"));
    }

    @GetMapping(value = ERROR_URL)
    @Operation(description = "error", hidden = true)
    public void handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(Objects.nonNull(status)){
            Object objErrorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            String errorMessage = StringUtils.defaultString((String) objErrorMessage);

            log.error("Status: {}. Error: {}", status, errorMessage);

            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()){
                throw new NotFoundException(errorMessage);
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
                throw new ServiceException(errorMessage);
            } else {
                throw new ServiceException("Status: " + status + " Error: " + StringUtils.defaultString(errorMessage));
            }
        }
    }

    @Override
    public String getErrorPath() {
        return ERROR_URL;
    }
}
