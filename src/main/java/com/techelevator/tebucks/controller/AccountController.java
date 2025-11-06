package com.techelevator.tebucks.controller;


import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.dao.UserDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.User;
import com.techelevator.tebucks.service.TearsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping(path = "/users")
    public List<User> getAllUsers(){
        return userDao.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path ="/transfer/{id}")
    public Transfer newTransfer(@RequestBody Transfer transfer, @PathVariable int id){
        Double amountToTransfer = transfer.getAmount();
        Account accountFrom = accountDao.getAccountByAccountId(transfer.getTransferUserFrom());
        Account accountTo = accountDao.getAccountByAccountId(transfer.getTransferUserTo());

        if(accountFrom.getBalance()<amountToTransfer){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient Funds");
        }
        try{
            accountDao.sendMoney(amountToTransfer, accountFrom.getUserId());
            accountDao.receiveMoney(amountToTransfer, accountTo.getUserId());

            Transfer newTransfer = transferDao.newTransfer(
                    amountToTransfer,
                    accountFrom.getUserId(),
                    accountTo.getUserId(),
                    "Send",
                    "Approved"
            );
            return newTransfer;
        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transfer failed: ", e);
        }
    }

    @GetMapping(path = "/transfer/userid/{userId}")
    public Transfer getAllTransfersSentOrReceived(@PathVariable int userId){
        List<Transfer>getAllTransfersSentOrReceived = null;
        try{
            return (Transfer) transferDao.getAllTransfers(userId);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @GetMapping(path = "transfer/id/{transferId}")
    public Transfer getTransferById (@PathVariable int transferId){
        try{
            return (Transfer) transferDao.getTransferById(transferId);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @GetMapping(path = "transfer/status/{transferStatus}")
    public Transfer viewTransfersByPending (@PathVariable String transferStatus){
        try{
            return (Transfer) transferDao.viewPendingTransferStatus(transferStatus);
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @PutMapping(path = "transfer/{transferId}/{transferStatus}")
    public Transfer updateTransferStatus(@PathVariable Integer transferId, String transferStatusUpdate, Principal principal){
        try{
            Transfer transfer = transferDao.getTransferById(transferId);
            if(transfer == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to locate transfer");
            }
            Account requesteeAccount = accountDao.getAccountByUserId(userDao.getUserByUsername(principal.getName()).getId());

            if("Approved".equalsIgnoreCase(transferStatusUpdate)){
                if(requesteeAccount.getBalance() < transfer.getAmount()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
                }
                accountDao.sendMoney(transfer.getAmount(), transfer.getTransferUserFrom());
                accountDao.receiveMoney(transfer.getAmount(), transfer.getTransferUserTo());
            } else if (!"Rejected".equalsIgnoreCase(transferStatusUpdate)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer status");
            }
            Transfer updatedTransfer = transferDao.updateMyPendingTransfer(transferId, transferStatusUpdate);
            return updatedTransfer;
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process request");
        }
    }

    @PostMapping(path = "/transfer/request/{requestFromUserId}")
    public Transfer requestTransfer(@RequestBody Double amount, Principal principal, @PathVariable int requestFromUserId){
            int requesterId = accountDao.getAccountByUserId(userDao.getUserByUsername(principal.getName()).getId()).getUserId();

            if(requesterId == requestFromUserId){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot request money from yourself");
            }
            if(amount == null || amount <= 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
            }

            try{
                Transfer transfer = transferDao.requestTransfer(
                        amount,
                        principal.getName(),
                        "Pending",
                        requestFromUserId,
                        requesterId
                );
                return transfer;
            } catch (DaoException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process your request");
            }
        }
    }
