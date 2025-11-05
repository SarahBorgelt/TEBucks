package com.techelevator.tebucks.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class Transfer {
    private int transferId;
    private String transferType;
    private String transferStatus;
    private Integer transferUserFrom;
    private Integer transferUserTo;
    private double amount;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getTransferUserFrom() {
        return transferUserFrom;
    }

    public void setTransferUserFrom(Integer transferUserFrom) {
        this.transferUserFrom = transferUserFrom;
    }

    public Integer getTransferUserTo() {
        return transferUserTo;
    }

    public void setTransferUserTo(Integer transferUserTo) {
        this.transferUserTo = transferUserTo;
    }
}
