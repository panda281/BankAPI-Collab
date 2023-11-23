package com.gebeya.bankapi.ServiceImpl;

import com.gebeya.bankapi.Exception.ErrorMessage;
import com.gebeya.bankapi.Model.DTO.CustomerInfoDTO;
import com.gebeya.bankapi.Model.DTO.ResponseModel;
import com.gebeya.bankapi.Model.Entities.MobileBankingUser;
import com.gebeya.bankapi.Model.Enums.CustomerProfile;
import com.gebeya.bankapi.Repository.AccountRepository;
import com.gebeya.bankapi.Repository.CustomerRepository;
import com.gebeya.bankapi.Repository.MobileBankingUserRepository;
import com.gebeya.bankapi.Service.MobileBankingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MobileBankingUserServiceImpl implements MobileBankingUserService {

    private AccountRepository accountRepository;

    private CustomerRepository customerRepository;
    private MobileBankingUserRepository mobileBankingUserRepository;

    @Autowired
    public MobileBankingUserServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, MobileBankingUserRepository mobileBankingUserRepository)
    {
        this.accountRepository=accountRepository;
        this.customerRepository=customerRepository;
        this.mobileBankingUserRepository=mobileBankingUserRepository;
    }

    @Override
    public ResponseModel activeMobileBanking(MobileBankingUsersDTO user)
    {
        Optional<CustomerInfoDTO> existingCustomer = customerRepository.findCustomerByAccountNo(user.getAccountNo());
        if(existingCustomer.isEmpty())
            throw new ErrorMessage(HttpStatus.NOT_FOUND,"Account could not be found");

        MobileBankingUser mobileBankingUser = new MobileBankingUser();
        mobileBankingUser.setPIN(user.getPin());
        if(user.getCustomerProfile().equals("default")||user.getCustomerProfile().equals("Default"))
        {
            mobileBankingUser.setCustomerProfile(CustomerProfile.Default);
        }
        else if(user.getCustomerProfile().equals("merchant")||user.getCustomerProfile().equals("Merchant"))
        {
            mobileBankingUser.setCustomerProfile(CustomerProfile.Merchant);
        }
        else{
            throw new ErrorMessage(HttpStatus.BAD_REQUEST,"Invalid request");
        }
        mobileBankingUser.setLanguage("en");
        mobileBankingUser.setVersion("v2");
        mobileBankingUser.setCustomer(customerRepository.findById(existingCustomer.get().getCif()).get());
        mobileBankingUserRepository.save(mobileBankingUser);
        return new ResponseModel(true,"account saved successfully");
    }

    public Iterable<MobileBankingUser> getAllMobileBankingUsers()
    {
        return mobileBankingUserRepository.findAll();
    }
}