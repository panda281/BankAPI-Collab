package com.gebeya.bankapi.Repository;

import com.gebeya.bankapi.Model.Entities.MobileBankingUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileBankingUserRepository extends CrudRepository<MobileBankingUser, Integer> {
    Optional<MobileBankingUser> findByUsernameAndCustomerMobileNo(String username, String customer_mobileNo);
}