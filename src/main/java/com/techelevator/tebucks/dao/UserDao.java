package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.RegisterUserDto;
import com.techelevator.tebucks.model.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int id);

    User getUserByUsername(String username);

    User createUser(RegisterUserDto user);
}
