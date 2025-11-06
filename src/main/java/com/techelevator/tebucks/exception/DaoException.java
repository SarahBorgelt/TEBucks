package com.techelevator.tebucks.exception;

import org.springframework.http.HttpStatus;

public class DaoException extends RuntimeException {
    public DaoException() {
        super();
    }
    public DaoException(String message) {
        super(message);
    }
    public DaoException(String message, Exception cause) {
        super(message, cause);
    }

    public DaoException(HttpStatus httpStatus) {
    }
}
