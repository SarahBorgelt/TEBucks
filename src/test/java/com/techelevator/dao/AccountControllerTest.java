package com.techelevator.dao;

import com.techelevator.tebucks.controller.AccountController;
import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.dao.UserDao;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.service.TearsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountControllerTest {
    private AccountDao accountDao;
    private UserDao userDao;
    private TransferDao transferDao;
    private TearsService tearsService;
    private AccountController accountController;
    private Principal principal;

    @BeforeEach
    void setup() {
        accountDao = mock(AccountDao.class);
        userDao = mock(UserDao.class);
        transferDao = mock(TransferDao.class);
        tearsService = mock(TearsService.class);

        accountController = new AccountController();
        accountController.setAccountDao(accountDao);
        accountController.setUserDao(userDao);
        accountController.setTransferDao(transferDao);
        accountController.setTearsService(tearsService);

        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
    }

    // Test getting balance successfully
    @Test
    void getBalance_returnsAccount() {
        Account mockAccount = new Account();
        mockAccount.setUserId(1);
        mockAccount.setBalance(100.0);
        when(accountDao.getAccountBalance("testUser")).thenReturn(mockAccount);

        Account result = accountController.getBalance(principal);

        assertNotNull(result);
        assertEquals(100.0, result.getBalance());
        verify(accountDao).getAccountBalance("testUser");
    }

    // Test getting all users
    @Test
    void getAllUsers_returnsListOfUsers() {
        User user1 = new User(); user1.setId(1); user1.setUsername("user1");
        User user2 = new User(); user2.setId(2); user2.setUsername("user2");
        when(userDao.getUsers()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = accountController.getAllUsers();

        assertEquals(2, users.size());
        verify(userDao).getUsers();
    }

    // Test creating a transfer with invalid amount
    @Test
    void createTransfer_invalidAmount_throwsException() {
        NewTransferDto dto = new NewTransferDto();
        dto.setUserFrom(1);
        dto.setUserTo(2);
        dto.setAmount(0); // Invalid amount
        dto.setTransferType("Send");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            accountController.createTransfer(dto, principal);
        });

        assertEquals("400 BAD_REQUEST \"Amount must be greater than zero\"", ex.getMessage());
    }

    // Test sending money when balance is sufficient
    @Test
    void createTransfer_sendMoney_success() {
        NewTransferDto dto = new NewTransferDto();
        dto.setUserFrom(1);
        dto.setUserTo(2);
        dto.setAmount(50.0);
        dto.setTransferType("Send");

        Account accountFrom = new Account();
        accountFrom.setUserId(1);
        accountFrom.setBalance(100.0);

        Account accountTo = new Account();
        accountTo.setUserId(2);
        accountTo.setBalance(0.0);

        Transfer transfer = new Transfer();
        transfer.setAmount(50.0);

        when(accountDao.getAccountByUserId(1)).thenReturn(accountFrom);
        when(accountDao.getAccountByUserId(2)).thenReturn(accountTo);
        when(userDao.getUserById(1)).thenReturn(new User());
        when(userDao.getUserById(2)).thenReturn(new User());
        when(transferDao.newTransfer(50.0, 1, 2, "Send", "Approved")).thenReturn(transfer);
        when(tearsService.shouldLogTransfer(any(), anyDouble())).thenReturn(false);

        Transfer result = accountController.createTransfer(dto, principal);

        assertNotNull(result);
        assertEquals(50.0, result.getAmount());
        verify(accountDao).sendMoney(50.0, 1);
        verify(accountDao).receiveMoney(50.0, 2);
        verify(transferDao).newTransfer(50.0, 1, 2, "Send", "Approved");
    }

    // Test sending money with insufficient funds triggers TEARS logging
    @Test
    void createTransfer_sendMoney_insufficientFunds_throwsException() {
        NewTransferDto dto = new NewTransferDto();
        dto.setUserFrom(1);
        dto.setUserTo(2);
        dto.setAmount(150.0);
        dto.setTransferType("Send");

        Account accountFrom = new Account();
        accountFrom.setUserId(1);
        accountFrom.setBalance(100.0);

        Account accountTo = new Account();
        accountTo.setUserId(2);
        accountTo.setBalance(0.0);

        when(accountDao.getAccountByUserId(1)).thenReturn(accountFrom);
        when(accountDao.getAccountByUserId(2)).thenReturn(accountTo);
        when(tearsService.getAuthToken()).thenReturn("token");
        when(userDao.getUserById(anyInt())).thenReturn(new User());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            accountController.createTransfer(dto, principal);
        });

        assertEquals("400 BAD_REQUEST \"Insufficient funds\"", ex.getMessage());
        verify(tearsService).logTransfer(any());
    }

    // Test getting all transfers for a user
    @Test
    void getAllTransfersSentOrReceived_returnsList() throws Exception {
        User user = new User(); user.setId(1);
        Transfer t1 = new Transfer();
        Transfer t2 = new Transfer();

        when(userDao.getUserByUsername("testUser")).thenReturn(user);
        when(transferDao.getAllTransfers(user)).thenReturn(Arrays.asList(t1, t2));

        List<Transfer> transfers = accountController.getAllTransfersSentOrReceived(principal);

        assertEquals(2, transfers.size());
        verify(transferDao).getAllTransfers(user);
    }

}
