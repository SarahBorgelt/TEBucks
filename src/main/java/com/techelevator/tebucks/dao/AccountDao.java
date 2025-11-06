package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

public interface AccountDao {
    //As an authenticated user of the system, I need to be able to see my Account Balance.
    Account getAccountBalance(String username);

    Account getAccountByUserId(int userId);

    Account getAccountByAccountId(int accountId);

    void sendMoney(Double amount, Integer fromUserId);

    void receiveMoney(Double amount, Integer toUserId);

}
