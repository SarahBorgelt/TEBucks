package com.techelevator.tebucks.controller;


import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.dao.UserDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.service.TearsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.loading.PrivateClassLoader;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping
public class AccountController {
    @Autowired
    AccountDao accountDao;

    @Autowired
    UserDao userDao;

    @Autowired
    TransferDao transferDao;

    @Autowired
    TearsService tearsService;

    @RequestMapping(path="/api/account/balance", method = RequestMethod.GET)
    public Account getBalance(Principal principal){
        System.out.println(principal.getName());
        return accountDao.getAccountBalance(principal.getName());
    }

    @GetMapping(path = "/api/users")
    public List<User> getAllUsers(){
        return userDao.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/api/transfers")
    public Transfer createTransfer(@RequestBody NewTransferDto newTransferDto, Principal principal) {
        if (newTransferDto.getUserFrom() == null || newTransferDto.getUserTo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User IDs must not be null");
        }
        if (newTransferDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        Account accountFrom = accountDao.getAccountByUserId(newTransferDto.getUserFrom());
        Account accountTo = accountDao.getAccountByUserId(newTransferDto.getUserTo());

        if ("Send".equalsIgnoreCase(newTransferDto.getTransferType())) {
            // Sending money
            if (accountFrom.getBalance() < newTransferDto.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
            }
            try {
                accountDao.sendMoney(newTransferDto.getAmount(), accountFrom.getUserId());
                accountDao.receiveMoney(newTransferDto.getAmount(), accountTo.getUserId());

                return transferDao.newTransfer(
                        newTransferDto.getAmount(),
                        accountFrom.getUserId(),
                        accountTo.getUserId(),
                        "Send",
                        "Approved"
                );
            } catch (DaoException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer failed", e);
            }
        } else if ("Request".equalsIgnoreCase(newTransferDto.getTransferType())) {
            // Requesting money
            if (newTransferDto.getUserFrom().equals(newTransferDto.getUserTo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot request money from yourself");
            }
            try {
                return transferDao.requestTransfer(
                        newTransferDto.getAmount(),
                        principal.getName(),
                        "Pending",
                        newTransferDto.getUserFrom(),
                        newTransferDto.getUserTo()
                );
            } catch (DaoException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create request", e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer type");
        }
    }


    @GetMapping(path = "/api/account/transfers")
    public List<Transfer> getAllTransfersSentOrReceived(Principal principal){
        try{
            User currentUser = userDao.getUserByUsername(principal.getName());
            return transferDao.getAllTransfers(currentUser);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @GetMapping(path = "/api/transfers/{id}")
    public Transfer getTransferById (@PathVariable int transferId){
        try{
            return transferDao.getTransferById(transferId);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @GetMapping(path = "/api/transfers/status/{transferStatus}")
    public List<Transfer> viewTransfersByPending (@PathVariable String transferStatus){
        try{
            String normalizedTransferStatus = transferStatus.trim();
            return transferDao.viewPendingTransferStatus(normalizedTransferStatus);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @PutMapping(path = "/api/transfers/{id}/status")
    public Transfer updateTransferStatus(@RequestBody TransferStatusUpdateDto transferStatusUpdateDto, Principal principal, @PathVariable Integer id){
        try{
            Transfer transfer = transferDao.getTransferById(id);
            if(transfer == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found");
            }

            String newStatus = transferStatusUpdateDto.getTransferStatus();

            if("Approved".equalsIgnoreCase(newStatus)){
                // Only send money if it was a Request
                if("Request".equalsIgnoreCase(transfer.getTransferType())){
                    Account payerAccount = accountDao.getAccountByUserId(transfer.getUserFrom().getId());
                    if(payerAccount.getBalance() < transfer.getAmount()){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
                    }
                    accountDao.sendMoney(transfer.getAmount(), transfer.getUserFrom().getId());
                    accountDao.receiveMoney(transfer.getAmount(), transfer.getUserTo().getId());
                }
            } else if(!"Rejected".equalsIgnoreCase(newStatus)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
            }

            return transferDao.updateMyPendingTransfer(id, newStatus);

        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }
    }
