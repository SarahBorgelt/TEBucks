package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
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
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if(results.next()){
            return mapResultsToAccount(results);
        } else {
            throw new DaoException("No account found for user: " + username);
        }
    }

    private Account mapResultsToAccount(SqlRowSet result){
        Account account = new Account();
        String accountBalance = result.getString("balance");
        if(accountBalance != null){
            account.setBalance(result.getDouble(accountBalance));
        } else {
            throw new DaoException("Unable to find account balance");
        }
        return new Account();
    }
}
