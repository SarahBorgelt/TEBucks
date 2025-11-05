package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

import java.util.List;

public class JdbcMemoryAccountDao implements AccountDao {
    private List<Account> account;
    private JdbcMemoryAccountDao accountDao;


    @Override
    public Account accountBalance() {

        return null;
    }

    @Override
    public List<Account> getAllTransfers() {
        return List.of();
    }

    @Override
    public Account getTransferById(int id) {
        for(Account transfer : account){

        }
        return null;
    }

    @Override
    public Account newTransfer(int amount) {
        return null;
    }

    @Override
    public Account updateTransfer(int amount) {
        return null;
    }

    @Override
    public List<Account> getAllUsers() {
        return List.of();
    }

    @Override
    public Account requestTransfer(int amount, String transferFromUser, String transferStatus, int transferFromUserId, int transferToUserId) {
        return null;
    }

    @Override
    public List<Account> viewPendingTransferStatus(String transferStatus) {
        return List.of();
    }

    @Override
    public Account updateMyPendingTransfer(String transferStatus) {
        return null;
    }
}
