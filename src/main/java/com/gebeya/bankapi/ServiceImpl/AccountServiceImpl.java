package com.gebeya.bankapi.ServiceImpl;

import com.gebeya.bankapi.Model.DTO.DefaultCustomerDTO;
import com.gebeya.bankapi.Model.DTO.MerchantCustomerDTO;
import com.gebeya.bankapi.Model.DTO.ResponseModel;
import com.gebeya.bankapi.Service.AccountService;

public class AccountServiceImpl implements AccountService {

    @Override
    public ResponseModel withdrawalForDefaultCustomer(DefaultCustomerDTO request) {
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


}
