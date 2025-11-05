package com.techelevator.tebucks.model;

/*
    The acronym DTO is being used for "data transfer object". It means that this type of class is specifically
    created to transfer data between the client and the server. For example, LoginDto represents the data a client
    must pass to the server for a login endpoint, and LoginResponseDto represents the object that's returned from the server
    to the client from a login endpoint.
 */

public class NewTransferDto {
    private int transferUserFrom;
    private int transferUserTo;
    private double transferAmount;
    private String transferType;




    public int getTransferUserFrom() {
        return transferUserFrom;
    }

    public void setTransferUserFrom(int transferUserFrom) {
        this.transferUserFrom = transferUserFrom;
    }

    public int getTransferUserTo() {
        return transferUserTo;
    }

    public void setTransferUserTo(int transferUserTo) {
        this.transferUserTo = transferUserTo;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
