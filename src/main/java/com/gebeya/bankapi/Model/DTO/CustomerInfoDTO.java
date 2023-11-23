package com.gebeya.bankapi.Model.DTO;

public class CustomerInfoDTO {
    private String firstName;
    private String mobileNo;
    private long cif;

    public CustomerInfoDTO() {
    }

    public CustomerInfoDTO(long cif, String firstName, String mobileNo) {
        this.firstName = firstName;
        this.mobileNo = mobileNo;
        this.cif = cif;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public long getCif() {
        return cif;
    }

    public void setCif(long cif) {
        this.cif = cif;
    }
}
