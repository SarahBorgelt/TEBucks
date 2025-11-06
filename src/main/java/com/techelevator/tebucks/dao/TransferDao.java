package com.techelevator.tebucks.dao;
import com.techelevator.tebucks.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getAllTransfers(Integer userId);

    Transfer getTransferById(Integer id);

    Transfer newTransfer(Double amount, Integer userFrom, Integer userTo, String transferType, String transferStatus);

    void updateTransfer(Transfer transfer);

    Transfer requestTransfer(Double amount, String transferFromUser, String transferStatus, int transferFromUserId, int transferToUserId);

    List<Transfer> viewPendingTransferStatus(String transferStatus);

    Transfer updateMyPendingTransfer(Integer transferId, String newStatus);

    Transfer getTransferStatus(String transferStatus, Integer userId, Integer transferID);
}
