package com.techelevator.tebucks.model;

/*
    The acronym DTO is being used for "data transfer object". It means that this type of class is specifically
    created to transfer data between the client and the server. For example, LoginDto represents the data a client
    must pass to the server for a login endpoint, and LoginResponseDto represents the object that's returned from the server
    to the client from a login endpoint.
 */

public class NewTransferDto {
    private int userFrom;
    private int userTo;
    private double amount;
    private String transferType;

    public int getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(int userFrom) {
        this.userFrom = userFrom;
    }

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
