package com.customermanagement.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c")
    List<Customer> findAllCustomers();

    @Query("select c from Customer c where c.id in (:id)")
    List<Customer> findCustomerById(List<Long> id);
}
