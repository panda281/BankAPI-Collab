package com.gebeya.bankapi.Model.Entities;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gebeya.bankapi.Model.Enums.CustomerProfile;
import jakarta.persistence.*;

@Entity
public class MobileBankingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private CustomerProfile customerProfile;
    private String username;
    private String PIN;
    private String password;
    private String version;
    private String language;
    @OneToOne
    @JoinColumn(name = "cif")
    @JsonManagedReference
    private Customer customer;

    public MobileBankingUser() {
    }

    public MobileBankingUser(CustomerProfile customerProfile, String username, String PIN, String password, String version, String language, Customer customer) {
        this.customerProfile = customerProfile;
        this.username = username;
        this.PIN = PIN;
        this.password = password;
        this.version = version;
        this.language = language;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "MobileBankingUser{" +
                "id=" + id +
                ", customerProfile='" + customerProfile + '\'' +
                ", username='" + username + '\'' +
                ", PIN=" + PIN +
                ", password='" + password + '\'' +
                ", version='" + version + '\'' +
                ", language='" + language + '\'' +
                ", customer=" + customer +
                '}';
    }
}

