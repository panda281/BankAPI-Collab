package com.gebeya.bankapi.Repository;



import com.gebeya.bankapi.Model.Entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account,Long> {

}