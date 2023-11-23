package com.gebeya.bankapi.Model.DTO;

public class DefaultCustomerDTO {
    private long defaultUserAccountNo;
    private double amount;

    public DefaultCustomerDTO() {
    }

    public DefaultCustomerDTO(long defaultUserAccountNo, double amount) {
        this.defaultUserAccountNo = defaultUserAccountNo;
        this.amount = amount;
    }

    public long getDefaultUserAccountNo() {
        return defaultUserAccountNo;
    }

    public void setDefaultUserAccountNo(long defaultUserAccountNo) {
        this.defaultUserAccountNo = defaultUserAccountNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DefaultCustomerDTO{" +
                "defaultUserAccountNo=" + defaultUserAccountNo +
                ", amount=" + amount +
                '}';
    }
}
