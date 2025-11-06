package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

public interface AccountDao {
    //As an authenticated user of the system, I need to be able to see my Account Balance.
    Account getAccountBalance(String username);


}
