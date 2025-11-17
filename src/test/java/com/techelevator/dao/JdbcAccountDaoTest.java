package com.techelevator.dao;


import com.techelevator.tebucks.dao.JdbcAccountDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JdbcAccountDaoTest {
    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;
    private SqlRowSet rowSet;

    @BeforeEach
    void setup() {
        // Mocking JdbcTemplate to simulate database queries
        jdbcTemplate = mock(JdbcTemplate.class);

        // Mocking SqlRowSet to simulate results returned by queries
        rowSet = mock(SqlRowSet.class);

        // Create the DAO instance with the mocked JdbcTemplate
        jdbcAccountDao = new JdbcAccountDao(jdbcTemplate);
    }

    // ------------------ getAccountBalance Tests ------------------

    @Test
    void getAccountBalance_returnsAccount_whenUserExists() {
        // Arrange: setup mocks to simulate a user with a valid account
        when(jdbcTemplate.queryForRowSet(anyString(), anyString())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(rowSet.getInt("account_id")).thenReturn(1);
        when(rowSet.getInt("user_id")).thenReturn(42);
        when(rowSet.getDouble("balance")).thenReturn(100.0);

        // Act: call the DAO method
        Account account = jdbcAccountDao.getAccountBalance("testUser");

        // Assert: verify the returned Account matches expected values
        assertNotNull(account);
        assertEquals(1, account.getAccountId());
        assertEquals(42, account.getUserId());
        assertEquals(100.0, account.getBalance());
    }

    @Test
    void getAccountBalance_throwsDaoException_whenUserNotFound() {
        // Arrange: simulate no rows returned by the query
        when(jdbcTemplate.queryForRowSet(anyString(), anyString())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        // Act & Assert: should throw DaoException because user not found
        DaoException ex = assertThrows(DaoException.class, () ->
                jdbcAccountDao.getAccountBalance("nonExistentUser")
        );

        assertTrue(ex.getMessage().contains("No account found"));
    }

    // ------------------ getAccountByUserId Tests ------------------

    @Test
    void getAccountByUserId_returnsAccount_whenUserExists() {
        // Arrange: mock query results for a user
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(rowSet.getInt("account_id")).thenReturn(2);
        when(rowSet.getInt("user_id")).thenReturn(99);
        when(rowSet.getDouble("balance")).thenReturn(200.0);

        // Act
        Account account = jdbcAccountDao.getAccountByUserId(99);

        // Assert: verify returned account details
        assertNotNull(account);
        assertEquals(2, account.getAccountId());
        assertEquals(99, account.getUserId());
        assertEquals(200.0, account.getBalance());
    }

    @Test
    void getAccountByUserId_throwsDaoException_whenUserNotFound() {
        // Arrange: simulate no rows found
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        // Act & Assert: DaoException expected
        DaoException ex = assertThrows(DaoException.class, () ->
                jdbcAccountDao.getAccountByUserId(999)
        );

        assertTrue(ex.getMessage().contains("No account found"));
    }

    // ------------------ getAccountByAccountId Tests ------------------

    @Test
    void getAccountByAccountId_returnsAccount_whenAccountExists() {
        // Arrange: mock a successful account retrieval
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(true);
        when(rowSet.getInt("account_id")).thenReturn(5);
        when(rowSet.getInt("user_id")).thenReturn(88);
        when(rowSet.getDouble("balance")).thenReturn(300.0);

        // Act
        Account account = jdbcAccountDao.getAccountByAccountId(5);

        // Assert
        assertNotNull(account);
        assertEquals(5, account.getAccountId());
        assertEquals(88, account.getUserId());
        assertEquals(300.0, account.getBalance());
    }

    @Test
    void getAccountByAccountId_throwsDaoException_whenAccountNotFound() {
        // Arrange: no results returned
        when(jdbcTemplate.queryForRowSet(anyString(), anyInt())).thenReturn(rowSet);
        when(rowSet.next()).thenReturn(false);

        // Act & Assert: should throw exception
        DaoException ex = assertThrows(DaoException.class, () ->
                jdbcAccountDao.getAccountByAccountId(1234)
        );

        assertTrue(ex.getMessage().contains("No account found"));
    }

    // ------------------ sendMoney Tests ------------------

    @Test
    void sendMoney_updatesAccount_whenAmountValid() {
        // Arrange: simulate successful update
        when(jdbcTemplate.update(anyString(), anyDouble(), anyInt())).thenReturn(1);

        // Act & Assert: no exception should be thrown
        assertDoesNotThrow(() -> jdbcAccountDao.sendMoney(50.0, 10));

        // Verify that JdbcTemplate.update was called with correct parameters
        verify(jdbcTemplate, times(1)).update(anyString(), eq(50.0), eq(10));
    }

    @Test
    void sendMoney_throwsIllegalArgumentException_whenAmountInvalid() {
        // Test negative, zero, and null amounts
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.sendMoney(-10.0, 1));
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.sendMoney(0.0, 1));
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.sendMoney(null, 1));
    }

    @Test
    void sendMoney_throwsDaoException_whenNoRowsUpdated() {
        // Arrange: simulate update failing (0 rows affected)
        when(jdbcTemplate.update(anyString(), anyDouble(), anyInt())).thenReturn(0);

        // Act & Assert: should throw DaoException
        DaoException ex = assertThrows(DaoException.class, () -> jdbcAccountDao.sendMoney(50.0, 10));
        assertTrue(ex.getMessage().contains("Unable to process request"));
    }

    // ------------------ receiveMoney Tests ------------------

    @Test
    void receiveMoney_updatesAccount_whenAmountValid() {
        // Arrange: simulate successful update
        when(jdbcTemplate.update(anyString(), anyDouble(), anyInt())).thenReturn(1);

        // Act & Assert: no exception expected
        assertDoesNotThrow(() -> jdbcAccountDao.receiveMoney(75.0, 20));

        // Verify update call with correct parameters
        verify(jdbcTemplate, times(1)).update(anyString(), eq(75.0), eq(20));
    }

    @Test
    void receiveMoney_throwsIllegalArgumentException_whenAmountInvalid() {
        // Test negative, zero, and null amounts
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.receiveMoney(-10.0, 1));
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.receiveMoney(0.0, 1));
        assertThrows(IllegalArgumentException.class, () -> jdbcAccountDao.receiveMoney(null, 1));
    }

    @Test
    void receiveMoney_throwsDaoException_whenNoRowsUpdated() {
        // Arrange: simulate update failing (0 rows affected)
        when(jdbcTemplate.update(anyString(), anyDouble(), anyInt())).thenReturn(0);

        // Act & Assert: should throw DaoException
        DaoException ex = assertThrows(DaoException.class, () -> jdbcAccountDao.receiveMoney(75.0, 20));
        assertTrue(ex.getMessage().contains("Unable to transfer funds"));
    }
}
