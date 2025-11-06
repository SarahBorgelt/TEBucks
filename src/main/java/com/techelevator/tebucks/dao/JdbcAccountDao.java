package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account getAccountBalance(String username) {
        String sql = "SELECT balance FROM account\n" +
                "JOIN tebuckers_user ON account.user_id = tebuckers_user.user_id\n" +
                "WHERE username = ?;\n;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if(results.next()){
                return mapResultsToAccount(results);
            } else {
                throw new DaoException("No account found for user: " + username);
            }
        } catch(CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to the database", e);
        }

    }

    @Override
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
            Account account = null;
            if(result.next()){
                account= mapResultsToAccount(result);
            } else {
                throw new DaoException("No account found for user ID: " + userId);
            }
            return account;
        } catch (DataAccessException e){
            throw new DaoException("Error retrieving account by user ID", e);
        }


    }

    @Override
    public Account getAccountByAccountId(int accountId) {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId);
            Account account = null;
            if(result.next()){
                account=mapResultsToAccount(result);
            } else {
                throw new DaoException("No account found for account ID: "+ accountId);
            }
            return account;
        } catch(DataAccessException e){
            throw new DaoException("Error retrieving account by account Id.", e);
        }
    }

    private Account mapResultsToAccount(SqlRowSet result){
        Account account = new Account();
        String accountBalance = result.getString("balance");
        if(accountBalance != null){
            account.setBalance(result.getDouble("balance"));
        } else {
            throw new DaoException("Unable to find account balance");
        }
        return account;
    }

    @Override
    public void sendMoney(Double amount, Integer fromUserId) {
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ?;";
        int rowsUpdated = jdbcTemplate.update(sql, amount, fromUserId);

        if (rowsUpdated==0){
            throw new DaoException("Unable to process request at this time");
        }
    }

    @Override
    public void receiveMoney(Double amount, Integer toUserId) {
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        String sql = "UPDATE account SET balance = balance + ? WHERE user_id = ?;";
        int rowsUpdated = jdbcTemplate.update(sql, amount, toUserId);

        if (rowsUpdated == 0){
            throw new DaoException("Unable to transfer funds to user");
        }
    }


}
