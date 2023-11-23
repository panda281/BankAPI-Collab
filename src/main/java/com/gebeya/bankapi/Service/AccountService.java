package com.gebeya.bankapi.Service;

import com.gebeya.bankapi.Model.DTO.*;
import com.gebeya.bankapi.Model.Entities.Account;
import com.gebeya.bankAPI.Model.DTO.TransferDTO;



public interface AccountService {
    public ResponseModel addAccount(Account Account);

    public ResponseModel checkBalance(long accountNo);

    public ResponseModel transfer(TransferDTO transferDTO);



    public CustomerProfileByAccountDTO customerProfileExtractor(long accountNo);



    public ResponseModel updateAccountCustomer(long accountId, Account account);

    public TopUpResponseDTO topUp(topUpRequestDTO topup);

    public ResponseModel deleteAccountCustomer(long AccountNo);

    public ResponseModel withdrawalForDefaultCustomer(DefaultCustomerDTO request);

    public ResponseModel withdrawalForMerchantCustomer(MerchantCustomerDTO request);

    public ResponseModel depositForDefaultCustomer (DefaultCustomerDTO request);

    public ResponseModel depositForMerchantCustomer (MerchantCustomerDTO request);



}