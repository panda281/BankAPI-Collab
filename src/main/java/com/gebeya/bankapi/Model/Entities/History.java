package com.gebeya.bankapi.Model.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gebeya.bankapi.Model.Enums.ResponseCode;
import com.gebeya.bankapi.Model.Enums.SIDE;
import com.gebeya.bankapi.Model.Enums.TransactionCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "rrn")
    private Transaction transaction;
    private TransactionCode transactionCode;
    @ManyToOne
    private Account account;
    private SIDE side;
    private double amount;
    private ResponseCode responseCode;
    private String phoneNo;
    private LocalDateTime transactionDate;

    public History() {
    }

    //for financial services when their transaction are failed
    public History(TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode, String phoneNo) {
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.amount = amount;
        this.responseCode = responseCode;
        this.phoneNo = phoneNo;

    }

    public History(TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode) {
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.amount = amount;
        this.responseCode = responseCode;

    }

    public History(TransactionCode transactionCode, Account account, SIDE side, ResponseCode responseCode) {
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.responseCode = responseCode;

    }

    //for financial services when their transactions are successful


    public History(Transaction transaction, TransactionCode transactionCode, Account account, SIDE side, double amount, ResponseCode responseCode, String phoneNo) {
        this.transaction = transaction;
        this.transactionCode = transactionCode;
        this.account = account;
        this.side = side;
        this.amount = amount;
        this.responseCode = responseCode;
        this.phoneNo = phoneNo;
    }



    //for non financial service
    public History(String phoneNo,ResponseCode responseCode,TransactionCode transactionCode,Account account)
    {
        this.phoneNo = phoneNo;
        this.responseCode=responseCode;
        this.transactionCode=transactionCode;
        this.account=account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @PrePersist
    protected void onTransaction(){
        transactionDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", transactionCode=" + transactionCode +
                ", account=" + account +
                ", side=" + side +
                ", amount=" + amount +
                ", responseCode=" + responseCode +
                ", phoneNo='" + phoneNo + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
