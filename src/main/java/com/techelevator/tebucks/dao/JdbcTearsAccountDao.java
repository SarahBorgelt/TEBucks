package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.TearsAccount;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTearsAccountDao implements TearsAccountDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTearsAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TearsAccount getUserById(int userId) {
        String sql = "SELECT * FROM ";
    }

    @Override
    public TearsAccount create(TearsAccount tearsAccount) {
        return null;
    }
}
