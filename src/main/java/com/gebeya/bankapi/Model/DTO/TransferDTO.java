package com.gebeya.bankAPI.Model.DTO;

public class TransferDTO {
    private long senderAccountNo;
    private long receiverAccountNo;
    private double amount;

    public TransferDTO() {
    }

    public TransferDTO(long senderAccountNo, long receiverAccountNo, double amount) {
        this.senderAccountNo = senderAccountNo;
        this.receiverAccountNo = receiverAccountNo;
        this.amount = amount;
    }

    public long getSenderAccountNo() {
        return senderAccountNo;
    }

    public void setSenderAccountNo(long senderAccountNo) {
        this.senderAccountNo = senderAccountNo;
    }

    public long getReceiverAccountNo() {
        return receiverAccountNo;
    }

    public void setReceiverAccountNo(long receiverAccountNo) {
        this.receiverAccountNo = receiverAccountNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
