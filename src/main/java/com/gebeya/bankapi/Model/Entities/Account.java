package com.gebeya.bankapi.Model.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gebeya.bankapi.Model.Enums.AccountStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSequenceGenerator")
    @SequenceGenerator(name = "accountSequenceGenerator", sequenceName = "account_sequence",allocationSize = 1, initialValue = 10000000)
    private Long accountNo;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "cif")
//    @JsonManagedReference
    private Customer customer;
    private double balance;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference
    private List<Transaction> transaction;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<History> history;



    public Account() {
    }

    public Account(double balance, AccountStatus accountStatus, Customer customer) {

        this.balance = balance;
        this.accountStatus = accountStatus;
        this.customer = customer;
    }

    public Account(Account account) {
        this.balance = account.getBalance();
        this.accountStatus = account.getAccountStatus();
        this.customer = account.getCustomer();
    }

    public Long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(Long accountNo) {
        this.accountNo = accountNo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNo=" + accountNo +
                ", customer=" + customer +
                ", balance=" + balance +
                ", accountStatus='" + accountStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", transaction=" + transaction +
                '}';
    }
}
