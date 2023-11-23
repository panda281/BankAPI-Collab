package com.gebeya.bankapi.Model.DTO;

import com.gebeya.bankapi.Model.Enums.SIDE;
import com.gebeya.bankapi.Model.Enums.TransactionCode;

import java.time.LocalDateTime;

public class ShortStatementDTO {
    private int rrn;
    private double amount;
    private SIDE side;
    private TransactionCode transactionCode;
    private LocalDateTime transactionDate;

    public ShortStatementDTO() {
    }

    public ShortStatementDTO(int rrn, double amount, SIDE side, TransactionCode transactionCode, LocalDateTime transactionDate) {
        this.rrn = rrn;
        this.amount = amount;
        this.side = side;
        this.transactionCode = transactionCode;
        this.transactionDate = transactionDate;
    }

    public int getRrn() {
        return rrn;
    }

    public void setRrn(int rrn) {
        this.rrn = rrn;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public SIDE getSide() {
        return side;
    }

    public void setSide(SIDE side) {
        this.side = side;
    }

    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "ShortStatementDTO{" +
                "rrn=" + rrn +
                ", amount=" + amount +
                ", side=" + side +
                ", transactionCode=" + transactionCode +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
