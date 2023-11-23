package com.gebeya.bankapi.Model.DTO;


import com.gebeya.bankapi.Model.Enums.CustomerProfile;

public class CustomerProfileByAccountDTO {
    private long cif;
    private CustomerProfile customerProfile;
    private String mobileNo;

    public CustomerProfileByAccountDTO() {
    }

    public CustomerProfileByAccountDTO(long cif,CustomerProfile customerProfile, String mobileNo) {
        this.cif = cif;
        this.customerProfile = customerProfile;
        this.mobileNo = mobileNo;
    }
    public CustomerProfileByAccountDTO(CustomerProfileByAccountDTO customerprofilebyaccountdto) {
        this.cif = customerprofilebyaccountdto.getCif();
        this.customerProfile = customerprofilebyaccountdto.getCustomerProfile();
        this.mobileNo = customerprofilebyaccountdto.getMobileNo();
    }

    public long getCif() {
        return cif;
    }

    public void setCif(long cif) {
        this.cif = cif;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }
}