package com.gebeya.bankapi.Model.DTO;

public class MobileBankingUsersDTO {
    private long accountNo;
    private String customerProfile;
    private String pin;

    public MobileBankingUsersDTO() {
    }

    public MobileBankingUsersDTO(long accountNo, String customerProfile, String pin) {
        this.accountNo = accountNo;
        this.customerProfile = customerProfile;
        this.pin = pin;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(long accountNo) {
        this.accountNo = accountNo;
    }

    public String getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(String customerProfile) {
        this.customerProfile = customerProfile;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
