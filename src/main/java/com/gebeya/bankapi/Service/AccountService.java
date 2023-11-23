package com.gebeya.bankapi.Service;

import com.gebeya.bankapi.Model.DTO.*;
import com.gebeya.bankapi.Model.Entities.Account;

public interface AccountService {
    public ResponseModel depositForMerchantCustomer (MerchantCustomerDTO request);
    public ResponseModel depositForDefaultCustomer (DefaultCustomerDTO request);

    public ResponseModel withdrawalForDefaultCustomer(DefaultCustomerDTO request);

    public ResponseModel withdrawalForMerchantCustomer(MerchantCustomerDTO request);
}
