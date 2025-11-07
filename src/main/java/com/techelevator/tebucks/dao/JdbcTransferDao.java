package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferType(rs.getString("transfer_type"));
        transfer.setTransferStatus(rs.getString("transfer_status"));
        transfer.setUserFrom(rs.getInt("user_from_id"));
        transfer.setUserTo(rs.getInt("user_to_id"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }

    @Override
    public List<Transfer> getAllTransfers(Integer userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE user_from_id = ? OR user_to_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public Transfer getTransferById(Integer id) {
        if(id == null){
            throw new IllegalArgumentException("Transfer ID cannot be null");
        }
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                transfer = mapRowToTransfer(results);
            }
        } catch(CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public Transfer newTransfer(Double amount, Integer userFrom, Integer userTo, String transferType, String transferStatus) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (userFrom.equals(userTo)) {
            throw new IllegalArgumentException("You cannot send money to yourself");
        }

        String sql = "INSERT INTO transfer(transfer_type, transfer_status, user_from_id, user_to_id, amount) VALUES(?,?,?,?,?) RETURNING transfer_id;";
        Integer newId;
        try {
            newId = jdbcTemplate.queryForObject(sql, Integer.class, transferType, transferStatus, userFrom, userTo, amount);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return getTransferById(newId);
    }


    @Override
    public void updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfers SET transfer_status = ? WHERE transfer_id = ?;";
        try{
            jdbcTemplate.update(sql, transfer.getTransferStatus(), transfer.getTransferId());
        } catch(CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Transfer requestTransfer(Double amount, String transferFromUser, String transferStatus, int transferFromUserId, int transferToUserId) {
        if(amount<=0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        };
        if(transferFromUserId == transferToUserId){
            throw new IllegalArgumentException("You cannot request money from yourself");
        }

        String sql ="INSERT INTO transfer(transfer_type, transfer_status, user_from_id, user_to_id, amount)" +
                "VALUES('Request', 'Pending',?,?,?) RETURNING transfer_id;";
        try{
            Integer newId = jdbcTemplate.queryForObject(sql, Integer.class, transferFromUserId, transferToUserId, amount);
            return getTransferById(newId);
        } catch(CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Transfer> viewPendingTransferStatus(String transferStatus) {
        String sql = "SELECT * FROM transfer WHERE transfer_status = 'Pending';";
        List<Transfer> transfers = new ArrayList<>();

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatus);
        while(results.next()){
            transfers.add(mapRowToTransfer(results));
        } return transfers;
    }

    @Override
    public Transfer updateMyPendingTransfer(Integer transferId, String newStatus) {
        if(transferId == null || newStatus==null){
            throw new IllegalArgumentException("TransferId and status cannot be null");
        }
        String sql = "UPDATE transfer SET transfer_status = ? WHERE transfer_id = ?;";

        try {
            int rowsUpdated = jdbcTemplate.update(sql, newStatus, transferId);

            if (rowsUpdated == 0) {
                throw new DaoException("No transfer found with ID: " + transferId);
            }

            return getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataAccessException e) {
            throw new DaoException("Error updating transfer in database", e);
        }
    }

    @Override
    public Transfer getTransferStatus(String transferStatus, Integer userId, Integer transferId) {
        String sql = "SELECT * FROM transfer WHERE transferStatus = ? AND (user_from_id = ? OR user_to_id = ?) AND transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferStatus, userId, userId, transferId);
        Transfer transferByStatus = null;
        if(result.next()){
            transferByStatus = mapRowToTransfer(result);
        } else {
            throw new DaoException("Unable to locate requested transfer");
        } return transferByStatus;
    }

}
