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


    public Transaction transactionSaverForDepositWithdrawal(TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode, int otp)
    {
        return new Transaction(transactionCode,account,side,amount,responseCode,otp);
    }

    private int otpGenerator()
    {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public boolean isOtpExpired(int otp){
        Optional<Transaction> existingTransaction = transactionRepository.findByOTP(otp);
        if(existingTransaction.isEmpty())
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Invalid OTP");

        LocalDateTime otpCreatedTime = existingTransaction.get().getTransactionDate();
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        Duration duration = Duration.between(otpCreatedTime,currentTimeStamp);
        long minuteDifference = duration.toMinutes();
        return minuteDifference >= 30;
    }


    @Override
    public ResponseModel depositForDefaultCustomer (DefaultCustomerDTO request)
    {
        if(!isTheAccountExists(request.getDefaultUserAccountNo()))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");

        CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getDefaultUserAccountNo());
        if(request.getAmount()<=0)
        {
            historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Credit,request.getAmount(),ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST, "amount must be greater than 0");
        }

        if(customerProfileByAccountDTO.getCustomerProfile()!= CustomerProfile.Default)
        {
            historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Credit,request.getAmount(),ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"bad request");
        }
        Account account = accountRepository.findById(request.getDefaultUserAccountNo()).get();
        Customer customerData = customerRepository.findById(customerProfileByAccountDTO.getCif()).get();

        if(customerData.getMobileNo()==null || Objects.equals(customerData.getMobileNo(), ""))
        {
            historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Credit,request.getAmount(),ResponseCode.failed,customerProfileByAccountDTO.getMobileNo()));
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer MobileNo could not be found");
        }


        int generatedOtp = otpGenerator();
        Transaction createdTransaction = transactionRepository.save(transactionSaverForDepositWithdrawal(TransactionCode.Deposit,account,SIDE.Credit, request.getAmount(), ResponseCode.pending,generatedOtp));
//            otpHandler(generatedOtp,customerData.getMobileNo());
        historyRepository.save(new History(createdTransaction,TransactionCode.Deposit,account,SIDE.Credit, request.getAmount(), ResponseCode.pending,customerProfileByAccountDTO.getMobileNo()));
        return new ResponseModel(true,"operation completed successfully. Your otp number is "+generatedOtp);

    }
    @Override
    public ResponseModel depositForMerchantCustomer (MerchantCustomerDTO request)
    {
        if(isOtpExpired(request.getOtp()))
        {
            throw new ErrorMessage(HttpStatus.BAD_REQUEST, "OTP expired");

        }


        if(!isTheAccountExists(request.getMerchantUserAccountNo()))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");
        //common
        CustomerProfileByAccountDTO McustomerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
        if(McustomerProfileByAccountDTO.getCustomerProfile()!=CustomerProfile.Merchant)
        {
            historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"bad request");
        }


        Account MerchantAccount = accountRepository.findById(request.getMerchantUserAccountNo()).get();
        if(request.getMobileNo()!=null)
        {
            Optional<Customer> existingAccount = customerRepository.findByMobileNo(request.getMobileNo());
            //not common
            if(existingAccount.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Mobile could not be found");
            }

            Optional<Transaction> DCustomer = transactionRepository.findByOTPAndMobileNo(request.getOtp(),request.getMobileNo());
            if(DCustomer.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Invalid OTP or MobileNo");
            }

            if(DCustomer.get().getResponseCode()!=ResponseCode.pending)
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"this transaction is already exist!!!");
            }

            Transaction DTransaction = DCustomer.get();
            Account DefaultAccount = accountRepository.findById(DTransaction.getAccount().getAccountNo()).get();

            if(DTransaction.getAmount()>MerchantAccount.getBalance())
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,DCustomer.get().getAmount(),ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Insufficient balance");
            }


            DefaultAccount.setBalance(DefaultAccount.getBalance()+DTransaction.getAmount());
            accountRepository.save(DefaultAccount);

            DTransaction.setResponseCode(ResponseCode.successful);
            transactionRepository.save(DTransaction);

            History existingHistory = historyRepository.findByRrn(DTransaction.getRrn());
            existingHistory.setResponseCode(ResponseCode.successful);
            historyRepository.save(existingHistory);

            MerchantAccount.setBalance(MerchantAccount.getBalance()-DTransaction.getAmount());
            accountRepository.save(MerchantAccount);
            CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
            Transaction mTransactionNew = transactionRepository.save(new Transaction(TransactionCode.Deposit,MerchantAccount,SIDE.Debit, DTransaction.getAmount(), ResponseCode.successful));
            historyRepository.save(new History(mTransactionNew,TransactionCode.Deposit,MerchantAccount,SIDE.Debit, DTransaction.getAmount(), ResponseCode.successful,customerProfileByAccountDTO.getMobileNo()));
            return new ResponseModel(true,"operation completed successfully.");
        }
        else if(request.getDefaultUserAccountNo()!=0)
        {
            if(!isTheAccountExists(request.getDefaultUserAccountNo()))
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");
            }


            Optional<Transaction> DCustomer = transactionRepository.findByOTPAndAccount_AccountNo(request.getOtp(),request.getDefaultUserAccountNo());
            if(DCustomer.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Invalid OTP or AccountNo");
            }
            if(DCustomer.get().getResponseCode()!=ResponseCode.pending)
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"this transaction is already exist!!!");
            }


            Transaction DTransaction = DCustomer.get();
            Account DefaultAccount = accountRepository.findById(request.getDefaultUserAccountNo()).get();

            if(DTransaction.getAmount()>MerchantAccount.getBalance())
            {
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Credit,DCustomer.get().getAmount(),ResponseCode.failed));
                historyRepository.save(new History(TransactionCode.Deposit,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Debit,DCustomer.get().getAmount(),ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Insufficient balance");
            }


            DefaultAccount.setBalance(DefaultAccount.getBalance()+DTransaction.getAmount());
            accountRepository.save(DefaultAccount);

            DTransaction.setResponseCode(ResponseCode.successful);
            transactionRepository.save(DTransaction);

            History existingHistory = historyRepository.findByRrn(DTransaction.getRrn());
            existingHistory.setResponseCode(ResponseCode.successful);
            historyRepository.save(existingHistory);


            MerchantAccount.setBalance(MerchantAccount.getBalance()-DTransaction.getAmount());
            accountRepository.save(MerchantAccount);
            CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
            Transaction mTransactionNew  = transactionRepository.save(new Transaction(TransactionCode.Deposit,MerchantAccount,SIDE.Debit,DTransaction.getAmount(),ResponseCode.successful));
            historyRepository.save(new History(mTransactionNew,TransactionCode.Deposit,MerchantAccount,SIDE.Debit, DTransaction.getAmount(), ResponseCode.successful,customerProfileByAccountDTO.getMobileNo()));
            return new ResponseModel(true,"operation completed successfully.");

        }
        else {
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"unknown error");
        }

    }




    @Override
    public ResponseModel withdrawalForDefaultCustomer(DefaultCustomerDTO request)
    {
        if(!isTheAccountExists(request.getDefaultUserAccountNo()))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");

        if(request.getAmount()<=0){
            throw new ErrorMessage( HttpStatus.BAD_REQUEST, "amount must be greater than 0");
        }
        CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getDefaultUserAccountNo());
        if(customerProfileByAccountDTO.getCustomerProfile()!= CustomerProfile.Default)
        {
            historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Debit,request.getAmount(),ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"bad request");
        }
        Account account = accountRepository.findById(request.getDefaultUserAccountNo()).get();
        Customer customerData = customerRepository.findById(customerProfileByAccountDTO.getCif()).get();
        if(account.getBalance()<=request.getAmount())
        {
            historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Debit,request.getAmount(),ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Insufficient balance");
        }


        if(customerData.getMobileNo()==null || Objects.equals(customerData.getMobileNo(), ""))
        {
            historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Debit,request.getAmount(),ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer MobileNo could not be found");
        }


        int generatedOtp = otpGenerator();
        Transaction createdTransaction = transactionRepository.save(transactionSaverForDepositWithdrawal(TransactionCode.Withdrawal,account,SIDE.Debit, request.getAmount(), ResponseCode.pending,generatedOtp));
//            otpHandler(generatedOtp,customerData.getMobileNo());
        historyRepository.save(new History(createdTransaction,TransactionCode.Withdrawal,account,SIDE.Debit, request.getAmount(),ResponseCode.successful, customerProfileByAccountDTO.getMobileNo()));
        return new ResponseModel(true,"operation completed successfully. Your OTP is "+generatedOtp);
    }

    @Override
    public ResponseModel withdrawalForMerchantCustomer(MerchantCustomerDTO request)
    {
        if(isOtpExpired(request.getOtp()))
            throw new ErrorMessage(HttpStatus.BAD_REQUEST, "OTP expired");

        if(!isTheAccountExists(request.getMerchantUserAccountNo()))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");

        CustomerProfileByAccountDTO McustomerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
        if(McustomerProfileByAccountDTO.getCustomerProfile()!=CustomerProfile.Merchant)
        {
            historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"bad request");
        }
        Account MerchantAccount = accountRepository.findById(request.getMerchantUserAccountNo()).get();
        if(request.getDefaultUserAccountNo()!=0)
        {

            if(!isTheAccountExists(request.getDefaultUserAccountNo()))
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer Account can not be found");
            }
            Optional<Transaction> DCustomer = transactionRepository.findByOTPAndAccount_AccountNo(request.getOtp(),request.getDefaultUserAccountNo());
            if(DCustomer.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Invalid OTP or AccountNo");
            }
            if(!(DCustomer.get().getResponseCode()==ResponseCode.pending))
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"this transaction is already exist!!!");
            }


            Transaction DTransaction = DCustomer.get();
            Account DefaultAccount = accountRepository.findById(request.getDefaultUserAccountNo()).get();

            if(DTransaction.getAmount()>DefaultAccount.getBalance())
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getDefaultUserAccountNo()).get(),SIDE.Debit,DCustomer.get().getAmount(),ResponseCode.failed));
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,DCustomer.get().getAmount(),ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Insufficient balance");
            }

            DefaultAccount.setBalance(DefaultAccount.getBalance()-DTransaction.getAmount());
            accountRepository.save(DefaultAccount);

            DTransaction.setResponseCode(ResponseCode.successful);
            transactionRepository.save(DTransaction);

            History existingHistory = historyRepository.findByRrn(DTransaction.getRrn());
            existingHistory.setResponseCode(ResponseCode.successful);
            historyRepository.save(existingHistory);

            MerchantAccount.setBalance(MerchantAccount.getBalance()+DTransaction.getAmount());
            accountRepository.save(MerchantAccount);
            CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
            Transaction mTransactionNew =  transactionRepository.save(new Transaction(TransactionCode.Withdrawal,MerchantAccount,SIDE.Credit,DTransaction.getAmount(),ResponseCode.successful));
            historyRepository.save(new History(mTransactionNew,TransactionCode.Withdrawal,MerchantAccount,SIDE.Credit, DTransaction.getAmount(), ResponseCode.successful,customerProfileByAccountDTO.getMobileNo()));
            return new ResponseModel(true,"operation completed successfully");


        } else if (request.getMobileNo()!=null) {
            Optional<Customer> existingAccount = customerRepository.findByMobileNo(request.getMobileNo());

            if(existingAccount.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Customer PhoneNumber could not be found");
            }


            Optional<Transaction> DCustomer = transactionRepository.findByOTPAndMobileNo(request.getOtp(),request.getMobileNo());
            if(DCustomer.isEmpty())
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.NOT_FOUND,"Invalid OTP or MobileNo");
            }
            if(!(DCustomer.get().getResponseCode()==ResponseCode.pending))
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"this transaction is already exist!!!");
            }
            Transaction DTransaction = DCustomer.get();
            Account DefaultAccount = accountRepository.findById(DTransaction.getAccount().getAccountNo()).get();

            if(DTransaction.getAmount()>DefaultAccount.getBalance())
            {
                historyRepository.save(new History(TransactionCode.Withdrawal,accountRepository.findById(request.getMerchantUserAccountNo()).get(),SIDE.Credit,DCustomer.get().getAmount(),ResponseCode.failed));
                throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Insufficient balance");
            }

            DefaultAccount.setBalance(DefaultAccount.getBalance()-DTransaction.getAmount());
            accountRepository.save(DefaultAccount);

            DTransaction.setResponseCode(ResponseCode.successful);
            transactionRepository.save(DTransaction);


            History existingHistory = historyRepository.findByRrn(DTransaction.getRrn());
            existingHistory.setResponseCode(ResponseCode.successful);
            historyRepository.save(existingHistory);

            MerchantAccount.setBalance(MerchantAccount.getBalance()+DTransaction.getAmount());
            accountRepository.save(MerchantAccount);
            CustomerProfileByAccountDTO customerProfileByAccountDTO = customerProfileExtractor(request.getMerchantUserAccountNo());
            Transaction mTransactionNew = transactionRepository.save(new Transaction(TransactionCode.Withdrawal,MerchantAccount,SIDE.Credit, DTransaction.getAmount(), ResponseCode.successful));
            historyRepository.save(new History(mTransactionNew,TransactionCode.Withdrawal,MerchantAccount,SIDE.Credit, DTransaction.getAmount(), ResponseCode.successful,customerProfileByAccountDTO.getMobileNo()));
            return new ResponseModel(true,"operation completed successfully");
        }
        else{
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"unknown error");
        }
    }


    //possible error on otpHandler .onErrorMap
    private void otpHandler(int otp, String mobileNo)
    {
        OtpRequestDTO requestBody = otpRequestDTOSetter(otp, mobileNo);
        Mono<String> responseBodyMono = otpHandler(requestBody);
        responseBodyMono
                .map(responseBody -> "Processed: " + responseBody)
                .doOnSuccess(result -> log.info("Account content: {}", result))
                .onErrorMap(Error -> new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during OTP sending"))
                .subscribe();
    }
    private boolean isTheAccountExists(long accountNo)
    {
        return accountRepository.existsById(accountNo);
    }
    private Mono<String> otpHandler(OtpRequestDTO requestDTO)
    {
        return webClientForOtp.post()
                .uri("/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDTO))
                .retrieve()
                .bodyToMono(String.class);
    }

    private OtpRequestDTO otpRequestDTOSetter(int otp, String phoneNo)
    {
        return new OtpRequestDTO("abinet","z]lY3Zl)St98T9(x.d",phoneNo,String.valueOf(otp),"otp");
    }

    @Override
    public TopUpResponseDTO topUp(topUpRequestDTO topup)
    {
        if(!isTheAccountExists(topup.getAccountNo()))
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"user account number could not be found");

        Account account = accountRepository.findById(topup.getAccountNo()).get();
        if(account.getBalance()<topup.getAmount())
            throw new ErrorMessage(HttpStatus.BAD_REQUEST, "Insufficient balance");
        TopUpResponseDTO returnedResponse = topUpfetch(topup.getAmount());
        account.setBalance(account.getBalance()-topup.getAmount());
        String phoneNo = customerProfileExtractor(topup.getAccountNo()).getMobileNo();
        accountRepository.save(account);
        Transaction createdTransaction = transactionRepository.save(new Transaction(TransactionCode.AirTimeTopUp,account,SIDE.Debit,topup.getAmount(),ResponseCode.successful));
        historyRepository.save(new History(createdTransaction,TransactionCode.AirTimeTopUp,account,SIDE.Debit,topup.getAmount(),ResponseCode.successful,phoneNo));
        return returnedResponse;


    }


    public TopUpResponseDTO topUpfetch(int paramValue)
    {
        TopUpResponseDTO jsonResponse = webClientForTopUp.get()
                .uri("/topup/"+paramValue)
                .retrieve()
                .onStatus(status ->status.is4xxClientError(), response->{
                    throw new ErrorMessage(HttpStatus.BAD_REQUEST,"bad request occurred");
                })
                .onStatus(status -> status.is5xxServerError(), response ->{
                    throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"unknown server error occurred. please try again later");
                })
                .bodyToMono(TopUpResponseDTO.class)
                .block();

        return jsonResponse;
    }
    @Override
    public ResponseModel deleteAccountCustomer(long AccountNo)
    {
        if(!isTheAccountExists(AccountNo))
        {
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Account could not be found");
        }


        customerRepository.deleteById(accountRepository.findById(AccountNo).get().getCustomer().getCif());
        accountRepository.deleteById(AccountNo);
        return new ResponseModel(true,"account deleted successfully");
    }


}