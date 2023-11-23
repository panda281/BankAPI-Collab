package com.gebeya.bankapi.Model.DTO;

public class MerchantCustomerDTO {
    private String mobileNo;
    private int otp;
    private long merchantUserAccountNo;
    private long defaultUserAccountNo;

    public MerchantCustomerDTO() {
    }

    public MerchantCustomerDTO(String mobileNo, int otp, long merchantUserAccountNo) {
        this.mobileNo = mobileNo;
        this.otp = otp;
        this.merchantUserAccountNo = merchantUserAccountNo;
    }

    public MerchantCustomerDTO(int otp, long merchantUserAccountNo, long defaultUserAccountNo) {
        this.otp = otp;
        this.merchantUserAccountNo = merchantUserAccountNo;
        this.defaultUserAccountNo = defaultUserAccountNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public long getMerchantUserAccountNo() {
        return merchantUserAccountNo;
    }

    public void setMerchantUserAccountNo(long merchantUserAccountNo) {
        this.merchantUserAccountNo = merchantUserAccountNo;
    }

    public long getDefaultUserAccountNo() {
        return defaultUserAccountNo;
    }

    public void setDefaultUserAccountNo(long defaultUserAccountNo) {
        this.defaultUserAccountNo = defaultUserAccountNo;
    }

    @Override
    public String toString() {
        return "MerchantCustomerDTO{" +
                "mobileNo='" + mobileNo + '\'' +
                ", otp=" + otp +
                ", merchantUserAccountNo=" + merchantUserAccountNo +
                ", defaultUserAccountNo=" + defaultUserAccountNo +
                '}';
    }
}
