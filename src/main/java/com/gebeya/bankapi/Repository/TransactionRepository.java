package com.gebeya.bankapi.Repository;

import com.gebeya.bankAPI.Model.DTO.ShortStatementDTO;
import com.gebeya.bankAPI.Model.Entities.Transaction;
import com.gebeya.bankAPI.Model.Enums.ResponseCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
    public Optional<Transaction> findByOTP(int otp);
    public Optional<Transaction> findByOTPAndAccount_AccountNo(int otp,Long accountNo);
    @Query(nativeQuery = true, value = "select t.rrn as rrn, t.amount as amount,t.response_code as response_code,t.side as side,t.transaction_code as transaction_code,t.transaction_date as transaction_date,t.account_no as account_no,t.otp as otp from transaction as t join account as a on t.account_no = a.account_no join customer as c on a.cif = c.cif where t.otp =:otp AND c.mobile_no =:customerMobileNo AND t.response_code = 'pending' ")
    public Optional<Transaction> findByOTPAndMobileNo(@Param("otp") int otp, @Param("customerMobileNo") String customerMobileNo);

    public List<Transaction> findAllByResponseCode(ResponseCode responseCode);

    @Query("SELECT new com.gebeya.bankAPI.Model.DTO.ShortStatementDTO(t.rrn, t.amount,t.side,t.transactionCode,t.transactionDate) FROM Transaction t WHERE t.account.accountNo = :accountNo ORDER BY t.transactionDate DESC LIMIT 5")
    List<ShortStatementDTO> findFirst5ByAccount_AccountNoOrderByTransactionDate(@Param("accountNo") long accountNo);
}