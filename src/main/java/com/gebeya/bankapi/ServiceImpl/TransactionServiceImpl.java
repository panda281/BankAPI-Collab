package com.gebeya.bankAPI.ServiceImpl;

import com.gebeya.bankAPI.Exception.ErrorMessage;
import com.gebeya.bankAPI.Model.DTO.*;
import com.gebeya.bankAPI.Model.Entities.History;
import com.gebeya.bankAPI.Model.Entities.Transaction;
import com.gebeya.bankAPI.Model.Enums.ResponseCode;
import com.gebeya.bankAPI.Model.Enums.TransactionCode;
import com.gebeya.bankAPI.Repository.AccountRepository;
import com.gebeya.bankAPI.Repository.CustomerRepository;
import com.gebeya.bankAPI.Repository.HistoryRepository;
import com.gebeya.bankAPI.Repository.TransactionRepository;
import com.gebeya.bankAPI.Service.AccountService;
import com.gebeya.bankAPI.Service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

import static reactor.core.publisher.Signal.subscribe;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;
    AccountRepository accountRepository;
    CustomerRepository customerRepository;
    AccountService accountService;
    HistoryRepository historyRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, CustomerRepository customerRepository, AccountService accountService, HistoryRepository historyRepository)
    {
        this.transactionRepository=transactionRepository;
        this.accountRepository= accountRepository;
        this.customerRepository = customerRepository;
        this.accountService=accountService;
        this.historyRepository = historyRepository;
    }

    public Transaction addTransaction(Transaction transaction)
    {
        return transactionRepository.save(transaction);
    }

    @Override
    @Scheduled(fixedRate = 120000)

    public void autoCheckOTPExpiration()
    {
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("Account content: {}", "scheduler is working...");
        List<Transaction> transactionList = transactionRepository.findAllByResponseCode(ResponseCode.pending);

        for(Transaction transaction: transactionList)
        {
            LocalDateTime otpCreationTime = transaction.getTransactionDate();
            Duration duration = Duration.between(otpCreationTime,currentTime);
            long minuteDifference = duration.toMinutes();
            if(minuteDifference>30){
                transaction.setResponseCode(ResponseCode.failed);
                Transaction t= transactionRepository.save(transaction);
                History h= historyRepository.findByRrn(t.getRrn());
                h.setResponseCode(ResponseCode.failed);
                historyRepository.save(h);
            }

        }

    }


    public List<Transaction> listAllTransactions()
    {
        return transactionRepository.findAll();
    }






    @Override
    public List<ShortStatementDTO> shortStatement(long accountNo)
    {
        if(!accountRepository.existsById(accountNo))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Account could not be found");
            String phoneNo = accountService.customerProfileExtractor(accountNo).getMobileNo();
        historyRepository.save(new History(phoneNo,ResponseCode.successful,TransactionCode.ShortStatement,accountRepository.findById(accountNo).get()));
        List<ShortStatementDTO> shortStatements = transactionRepository.findFirst5ByAccount_AccountNoOrderByTransactionDate(accountNo);
        if(shortStatements.isEmpty())
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Transaction is empty");
        return transactionRepository.findFirst5ByAccount_AccountNoOrderByTransactionDate(accountNo);
    }





}
