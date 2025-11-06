package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.TearsAccount;

public interface TearsAccountDao {
    TearsAccount getUserById(int userId);
    TearsAccount create(TearsAccount tearsAccount);
}
