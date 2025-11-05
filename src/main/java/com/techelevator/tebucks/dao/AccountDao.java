package com.techelevator.tebucks.dao;
import com.techelevator.tebucks.model.Account;

import java.util.List;

public interface AccountDao {

    Account accountBalance();

    List<Account> getAllTransfers();

    Account getTransferById(int id);

    Account newTransfer(int amount);

    Account updateTransfer(int amount);

    List<Account> getAllUsers();

    Account requestTransfer(int amount, String transferFromUser, String transferStatus, int transferFromUserId, int transferToUserId);

    List<Account> viewPendingTransferStatus(String transferStatus);

    Account updateMyPendingTransfer(String transferStatus);








}
