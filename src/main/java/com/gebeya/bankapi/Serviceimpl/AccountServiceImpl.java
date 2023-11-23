package com.gebeya.bankapi.Serviceimpl;
import com.gebeya.bankapi.Exception.ErrorMessage;
import com.gebeya.bankapi.Model.DTO.CustomerProfileByAccountDTO;
import com.gebeya.bankapi.Model.DTO.DefaultCustomerDTO;
import com.gebeya.bankapi.Model.DTO.MerchantCustomerDTO;
import com.gebeya.bankapi.Model.DTO.ResponseModel;
import com.gebeya.bankapi.Model.Entities.Account;
import com.gebeya.bankapi.Model.Entities.Customer;
import com.gebeya.bankapi.Model.Entities.History;
import com.gebeya.bankapi.Model.Entities.Transaction;
import com.gebeya.bankapi.Model.Enums.*;
import com.gebeya.bankapi.Repository.AccountRepository;
import com.gebeya.bankapi.Repository.CustomerRepository;
import com.gebeya.bankapi.Repository.HistoryRepository;
import com.gebeya.bankapi.Repository.TransactionRepository;
import com.gebeya.bankapi.Service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    CustomerRepository customerRepository;
    TransactionRepository transactionRepository;
    HistoryRepository historyRepository;
    WebClient webClientForOtp;

    WebClient webClientForTopUp;

//    TransactionService transactionService;


    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRepository transactionRepository, HistoryRepository historyRepository, WebClient webClientForOtp,WebClient webClientForTopUp)
    {
        this.accountRepository=accountRepository;
        this.customerRepository=customerRepository;
        this.transactionRepository=transactionRepository;
        this.historyRepository = historyRepository;
        this.webClientForOtp =webClientForOtp;
        this.webClientForTopUp = webClientForTopUp;
    }

    @Override
    public ResponseModel addAccount(Account account){
        Customer customer = new Customer(account.getCustomer());
        Customer createdCustomer = customerRepository.save(customer);
        Account newAccount = new Account(account);
        newAccount.setAccountStatus(AccountStatus.Active);
        newAccount.setCustomer(createdCustomer);
        accountRepository.save(newAccount);
        return new ResponseModel(true, "Account created successfully");
    }

    @Override
    public ResponseModel updateAccountCustomer(long accountId, Account account){
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty())
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Account not found");
        Account existingAccount = optionalAccount.get();
        existingAccount.setBalance(account.getBalance());
        existingAccount.setAccountStatus(account.getAccountStatus());

        Customer existingCustomer = customerRepository.findById(existingAccount.getCustomer().getCif()).get();
        existingCustomer.setCity(account.getCustomer().getCity());
        existingCustomer.setCountry(account.getCustomer().getCountry());
        existingCustomer.setDob(account.getCustomer().getDob());
        existingCustomer.setEmail(account.getCustomer().getEmail());
        existingCustomer.setFirstName(account.getCustomer().getFirstName());
        existingCustomer.setHomePhone(account.getCustomer().getHomePhone());
        existingCustomer.setHomePostalAddress(account.getCustomer().getHomePostalAddress());
        existingCustomer.setLastName(account.getCustomer().getLastName());
        existingCustomer.setMiddleName(account.getCustomer().getMiddleName());
        existingCustomer.setMobileNo(account.getCustomer().getMobileNo());
        existingCustomer.setPostalCode(account.getCustomer().getPostalCode());
        existingCustomer.setSalutation(account.getCustomer().getSalutation());

        existingAccount.setCustomer(customerRepository.save(existingCustomer));
        accountRepository.save(existingAccount);

        return new ResponseModel(true, "Account and associated customer updated successfully");

    }

    @Override
    public ResponseModel checkBalance(long accountNo){
        if(!accountRepository.existsById(accountNo))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"invalid AccountNo");

        double amount = accountRepository.findById(accountNo).get().getBalance();
        CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(accountNo);

        historyRepository.save(new History(customerProfileByAccountDTO.getMobileNo(), ResponseCode.successful, TransactionCode.BalanceInquiry,accountRepository.findById(accountNo).get()));
        return new ResponseModel(true,"the current amount is "+amount);
    }

    @Override
    public ResponseModel transfer(TransferDTO transferDTO)
    {
        if(!(accountRepository.existsById(transferDTO.getSenderAccountNo()) && accountRepository.existsById(transferDTO.getReceiverAccountNo())))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"account could not be found");

        Account senderAccount = accountRepository.findById(transferDTO.getSenderAccountNo()).get();
        Account receiverAccount = accountRepository.findById(transferDTO.getReceiverAccountNo()).get();
        double amount= transferDTO.getAmount();
        CustomerProfileByAccountDTO  senderMobileNo = customerProfileExtractor(senderAccount.getAccountNo());
        CustomerProfileByAccountDTO  receiverMobileNo = customerProfileExtractor(receiverAccount.getAccountNo());

        if(amount<=0)
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"amount must be greater than 0");

        if(senderAccount.getAccountStatus().equals(AccountStatus.Blocked) || receiverAccount.getAccountStatus().equals(AccountStatus.Blocked))
        {
            historyRepository.save(new History(TransactionCode.Transfer,senderAccount, SIDE.Debit,transferDTO.getAmount(),ResponseCode.failed,senderMobileNo.getMobileNo()));
            historyRepository.save(new History(TransactionCode.Transfer,receiverAccount,SIDE.Credit,transferDTO.getAmount(),ResponseCode.failed,receiverMobileNo.getMobileNo()));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Account is blocked");
        }


        if(senderAccount.getBalance()<transferDTO.getAmount())
        {
            historyRepository.save(new History(TransactionCode.Transfer,senderAccount,SIDE.Debit,transferDTO.getAmount(),ResponseCode.failed,senderMobileNo.getMobileNo()));
            historyRepository.save(new History(TransactionCode.Transfer,receiverAccount,SIDE.Credit,transferDTO.getAmount(),ResponseCode.failed,receiverMobileNo.getMobileNo()));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST, "Insufficient Balance");
        }


        senderAccount.setBalance(senderAccount.getBalance() - transferDTO.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + transferDTO.getAmount());

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transaction sTransaction = transactionRepository.save(new Transaction(TransactionCode.Transfer,senderAccount, SIDE.Debit,transferDTO.getAmount(), ResponseCode.successful));
        Transaction rTransaction = transactionRepository.save(new Transaction(TransactionCode.Transfer,receiverAccount,SIDE.Credit,transferDTO.getAmount(),ResponseCode.successful));

        History senderHistory = historySaver(sTransaction,TransactionCode.Transfer,senderAccount,SIDE.Debit, transferDTO.getAmount(), ResponseCode.successful,senderMobileNo.getMobileNo());
        historyRepository.save(senderHistory);

        History receiverHistory = historySaver(rTransaction,TransactionCode.Transfer,receiverAccount,SIDE.Credit, transferDTO.getAmount(), ResponseCode.successful,receiverMobileNo.getMobileNo());
        historyRepository.save(receiverHistory);

        return new ResponseModel(true,"transfer completed successfully");
    }

    public CustomerProfileByAccountDTO customerProfileExtractor(long accountNo){
        if(customerRepository.customerProfileExtractor(accountNo).isEmpty())
            return new CustomerProfileByAccountDTO();
        return new CustomerProfileByAccountDTO(customerRepository.customerProfileExtractor(accountNo).get());
    }

    public History historySaver(Transaction transaction,TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode, String mobileNo)
    {
        History history = new History();
        history.setTransaction(transaction);
        history.setTransactionCode(transactionCode);
        history.setAccount(account);
        history.setSide(side);
        history.setAmount(amount);
        history.setResponseCode(responseCode);
        history.setPhoneNo(mobileNo);
        return history;
    }
}