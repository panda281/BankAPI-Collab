package com.gebeya.bankapi.Model.Entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cif;
    private String firstName;
    private String salutation;
    private String middleName;
    private String lastName;
    private String email;
    private Date dob;
    private String homePostalAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String homePhone;
    //        @UniqueConstraint()
    private String mobileNo;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Account> account;


    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonBackReference
    @JsonIgnore
    private MobileBankingUser user;

    public Customer() {
    }

    public Customer(String firstName, String salutation, String middleName, String lastName, String email, Date dob, String homePostalAddress, String city, String state, String postalCode, String country, String homePhone, String mobileNo) {
        this.firstName = firstName;
        this.salutation = salutation;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
        this.homePostalAddress = homePostalAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.homePhone = homePhone;
        this.mobileNo = mobileNo;
    }

    public Customer(Customer customer) {
        this.firstName = customer.getFirstName();
        this.salutation = customer.salutation;
        this.middleName = customer.getMiddleName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.dob = customer.getDob();
        this.homePostalAddress = customer.getHomePostalAddress();
        this.city = customer.getCity();
        this.state = customer.getState();
        this.postalCode = customer.getPostalCode();
        this.country = customer.getCountry();
        this.homePhone = customer.getHomePhone();
        this.mobileNo = customer.getMobileNo();
    }

    public Long getCif() {
        return cif;
    }

    public void setCif(Long cif) {
        this.cif = cif;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getHomePostalAddress() {
        return homePostalAddress;
    }

    public void setHomePostalAddress(String homePostalAddress) {
        this.homePostalAddress = homePostalAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "cif=" + cif +
                ", firstName='" + firstName + '\'' +
                ", salutation='" + salutation + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob +
                ", homePostalAddress='" + homePostalAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", account=" + account +
                '}';
    }
}
