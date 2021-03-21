package com.customermanagement.controller;

import com.customermanagement.config.UriConfig;
import com.customermanagement.domain.CustomerDetails;
import com.customermanagement.exception.NotFoundException;
import com.customermanagement.exception.ServiceException;
import com.customermanagement.exception.domain.ApiErrors;
import com.customermanagement.persistence.entity.Customer;
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
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.customermanagement.utils.LinkUtils.buildSelfHateoasLinks;

@RestController
@Slf4j
@Tag(name = "Customer Management")
public class CustomerController implements ErrorController {

    private static final String ERROR_URL = "${server.error.path:${error.path:/error}}";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UriConfig uriConfig;

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
        List<CustomerDetails> customers = customerService.getAllCustomers().orElseThrow(() -> new NotFoundException("Customers not found"));
        if(!CollectionUtils.isEmpty(customers)){
            customers.forEach( c -> {
                Link link = buildSelfHateoasLinks(uriConfig, c, c.getId());
                c.add(link);
            });
        }
        return customers;
    }

    /**
     * Endpoint takes list of ids as input and return customer details of provided ids
     *
     * @param ids
     * @return CustomerDetails list
     */

    @Operation(operationId = "findByCustomerIds", description = "Customers Management", summary = "Find Customers by ids.",
                responses =  {
                        @ApiResponse(responseCode = "200", description = "successful response received", content = @Content(schema = @Schema(implementation = CustomerDetails.class))),
                        @ApiResponse(responseCode = "400", description = "Not Found error", content = @Content(schema = @Schema(implementation = ApiErrors.class))),
                        @ApiResponse(responseCode = "406", description = "Not Acceptable Error", content = @Content(schema = @Schema(implementation = ApiErrors.class))),
                        @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ApiErrors.class)))

                })
    @GetMapping(value = "${customer-management.findByIds}")
    public List<CustomerDetails> findByCustomerIds(
            @Parameter(name = "ids", in = ParameterIn.QUERY, description = "Customer Id")
            @RequestParam("ids") List<Long> ids){
        return customerService.getCustomersByIds(ids).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    /**
     * find customer by id
     *
     * @param id
     * @return CustomerDetials
     */
    @Operation(operationId = "findByCustomerId", description = "Customers Management", summary = "Find Customers by ids.",
            responses =  {
                    @ApiResponse(responseCode = "200", description = "successful response received", content = @Content(schema = @Schema(implementation = CustomerDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Not Found error", content = @Content(schema = @Schema(implementation = ApiErrors.class))),
                    @ApiResponse(responseCode = "406", description = "Not Acceptable Error", content = @Content(schema = @Schema(implementation = ApiErrors.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ApiErrors.class)))

            })
    @GetMapping(value = "${customer-management.findById}")
    public CustomerDetails findByCustomerId(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "Customer Id")
            @PathVariable("id")Long id){
        return customerService.getCustomersById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    /**
     * add new customer to db
     *
     * @param customer
     */
    @PostMapping("${customer-management.addCustomer}")
    public Customer addCustomer(@RequestBody Customer customer){
        return customerService.addCustomer(customer);
        //return "Customer added successfully with id "+customer.getId();
    }

    /**
     * update customer to db
     * @param customer
     */
    @PutMapping("${customer-management.updateCustomer}")
    public Customer updateCustomer(@RequestBody Customer customer){
        return customerService.updateCustomer(customer);
        //return "Customer updated successfully with id "+customer.getId();
    }

    /**
     * delete customer from db
     * @param id
     */
    @DeleteMapping("${customer-management.deleteCustomer}")
    public String deleteCustomer(@PathVariable("id") Long id){
        return customerService.deleteCustomer(id);
        //return "Customer deleted successfully with id "+id;
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
