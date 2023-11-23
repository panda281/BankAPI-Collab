package com.gebeya.bankapi.Service;

import com.gebeya.bankAPI.Model.DTO.ShortStatementDTO;
import com.gebeya.bankAPI.Model.Entities.Transaction;

import java.util.List;

public interface TransactionService {
    //    public Transaction addTransaction(TransactionRequestDTO transaction);
//    public Transaction updateTransaction(Transaction transaction);
//    @Scheduled(fixedRate = 60000)
    public void autoCheckOTPExpiration();
    public Transaction addTransaction(Transaction transaction);

    public List<ShortStatementDTO> shortStatement(long accountNo);

    public List<Transaction> listAllTransactions();
}