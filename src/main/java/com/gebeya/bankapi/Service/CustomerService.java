package com.gebeya.bankapi.Service;



import com.gebeya.bankapi.Model.DTO.ResponseModel;
import com.gebeya.bankapi.Model.Entities.Customer;

import java.util.Optional;

public interface CustomerService {

    public Iterable<Customer> getAllCustomer();

    public ResponseModel deleteCustomer(long CustomerNo);

    public Optional<Customer> findCustomerById(long id);

}
