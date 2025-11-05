package com.techelevator.tebucks.dao;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getAllTransfers();

    Transfer getTransferById(Integer id);

    Transfer newTransfer(Integer amount, Integer userFrom, Integer userTo, String transferType, String transferStatus);

    void updateTransfer(Transfer transfer);

    Transfer requestTransfer(int amount, String transferFromUser, String transferStatus, int transferFromUserId, int transferToUserId);

    List<Transfer> viewPendingTransferStatus(String transferStatus);

    Transfer updateMyPendingTransfer(Integer transferId, String newStatus);
}
