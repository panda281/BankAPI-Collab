package com.gebeya.bankAPI.ServiceImpl;

import com.gebeya.bankAPI.Exception.ErrorMessage;
import com.gebeya.bankAPI.Model.DTO.ResponseModel;
import com.gebeya.bankAPI.Model.Entities.Customer;
import com.gebeya.bankAPI.Repository.CustomerRepository;
import com.gebeya.bankAPI.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Spliterator;


@Service
public class CustomerServiceImpl implements CustomerService {


   CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository)
    {
        this.customerRepository=customerRepository;
    }

//    @Override
//    public Customer addCustomer(Customer customer)
//    {
//        return customerRepository.save(customer);
//    }

    public Optional<Customer> findCustomerById(long id)
    {
        if(!customerRepository.existsById(id))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"CustomerID could not be found");
        return customerRepository.findById(id);
    }

    @Override
    public Iterable<Customer> getAllCustomer(){
        Iterable<Customer> customer = customerRepository.findAll();
        Spliterator<Customer> customerSpliterator = customer.spliterator();
        if(!customerSpliterator.tryAdvance(cust -> {}))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"customers could not be found");
        return customer;
    }

    @Override
    public ResponseModel deleteCustomer(long CustomerNo){
        if(!customerRepository.existsById(CustomerNo))
        {
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Id could not be found");
        }
        customerRepository.deleteById(CustomerNo);

        return new ResponseModel(true, "Customer deleted successfully");
    }
}
