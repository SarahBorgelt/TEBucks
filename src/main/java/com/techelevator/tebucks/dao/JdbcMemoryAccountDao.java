package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcMemoryAccountDao implements AccountDao {
    private List<Account> account;
    private JdbcMemoryAccountDao accountDao;
    private final JdbcTemplate jdbcTemplate;

    public JdbcMemoryAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
    }

    @Override
    public Account accountBalance() {

        return null;
    }

    @Override
    public List<Account> getAllTransfers() {
        return null;

    }

    @Override
    public Account getTransferById(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("Transfer ID cannot be null");
        }
        Account transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                transfer = mapRowToAccount(results);
            }
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
