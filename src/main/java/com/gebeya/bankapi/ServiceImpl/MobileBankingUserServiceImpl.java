package com.gebeya.bankAPI.ServiceImpl;

import com.gebeya.bankAPI.Exception.ErrorMessage;
import com.gebeya.bankAPI.Model.DTO.MobileBankingUsersDTO;
import com.gebeya.bankAPI.Model.DTO.ResponseModel;
import com.gebeya.bankAPI.Model.DTO.CustomerInfoDTO;
import com.gebeya.bankAPI.Model.Entities.MobileBankingUser;
import com.gebeya.bankAPI.Model.Enums.CustomerProfile;
import com.gebeya.bankAPI.Repository.AccountRepository;
import com.gebeya.bankAPI.Repository.CustomerRepository;
import com.gebeya.bankAPI.Repository.MobileBankingUserRepository;
import com.gebeya.bankAPI.Service.MobileBankingUserService;
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
