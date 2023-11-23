package com.gebeya.bankapi.Repository;

import com.gebeya.bankAPI.Model.DTO.CustomerInfoDTO;
import com.gebeya.bankAPI.Model.DTO.CustomerProfileByAccountDTO;
import com.gebeya.bankAPI.Model.Entities.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {
    Optional<Customer> findByMobileNo(String mobileNo);
    @Query("SELECT new com.gebeya.bankAPI.Model.DTO.CustomerInfoDTO(c.cif,c.firstName,c.mobileNo) from Account as a join Customer as c on a.customer.cif = c.cif where a.accountNo = :accountNo")
    Optional<CustomerInfoDTO> findCustomerByAccountNo(@Param("accountNo") long accountNO);

    @Query("SELECT new com.gebeya.bankAPI.Model.DTO.CustomerProfileByAccountDTO(c.cif,m.customerProfile,c.mobileNo) from MobileBankingUser m join Customer  c on c.cif = m.customer.cif join Account  a on c.cif = a.customer.cif where a.accountNo = :accountNo")
    Optional<CustomerProfileByAccountDTO> customerProfileExtractor(@Param("accountNo") long accountNo);

}
