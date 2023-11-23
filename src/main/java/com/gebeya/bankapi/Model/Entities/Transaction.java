package com.gebeya.bankapi.Model.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gebeya.bankapi.Model.Enums.ResponseCode;
import com.gebeya.bankapi.Model.Enums.SIDE;
import com.gebeya.bankapi.Model.Enums.TransactionCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rrn;
    @Enumerated(EnumType.STRING)
    private TransactionCode transactionCode;
    @ManyToOne
    @JoinColumn(name = "accountNo")
    @JsonBackReference
    private Account account;
    private SIDE side;
    private double amount;
    @Enumerated(EnumType.STRING)
    private ResponseCode responseCode;
    private int OTP;
    private LocalDateTime transactionDate;
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    @JsonIgnore
    private History history;
    public Transaction() {
    }

    public Transaction(TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode, int OTP) {
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.amount = amount;
        this.OTP = OTP;
        this.responseCode = responseCode;
    }

    public Transaction(TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode) {
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.amount = amount;
        this.responseCode = responseCode;
    }

    public int getRrn() {
        return rrn;
    }

    public void setRrn(int rrn) {
        this.rrn = rrn;
    }

    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SIDE getSide() {
        return side;
    }

    public void setSide(SIDE side) {
        this.side = side;
    }

    public int getOTP() {
        return OTP;
    }

    public void setOTP(int OTP) {
        this.OTP = OTP;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    @PrePersist
    protected void onTransaction(){
        transactionDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "rrn='" + rrn + '\'' +
                ", transactionCode=" + transactionCode +
                ", accountName='" + account + '\'' +
                ", side='" + side + '\'' +
                ", OTP='" + OTP + '\'' +
                ", amount=" + amount +
                ", responseCode=" + responseCode +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
