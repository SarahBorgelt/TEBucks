package com.techelevator.tebucks.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class Transfer {
    private int transferId;
    private String transferType;
    private String transferStatus;
    private String transferUserFrom;
    private String transferUserTo;
    private Float amount;

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

    public String getTransferUserFrom() {
        return transferUserFrom;
    }

    public void setTransferUserFrom(String transferUserFrom) {
        this.transferUserFrom = transferUserFrom;
    }

    public String getTransferUserTo() {
        return transferUserTo;
    }

    public void setTransferUserTo(String transferUserTo) {
        this.transferUserTo = transferUserTo;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
