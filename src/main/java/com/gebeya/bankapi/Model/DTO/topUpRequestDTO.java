package com.gebeya.bankapi.Model.DTO;

public class topUpRequestDTO {
    private long accountNo;
    private int amount;

    public topUpRequestDTO(long accountNo, int amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(long accountNo) {
        this.accountNo = accountNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "topUpRequestDTO{" +
                "accountNo=" + accountNo +
                ", amount=" + amount +
                '}';
    }
}
