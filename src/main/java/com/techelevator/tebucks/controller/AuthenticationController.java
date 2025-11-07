package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.service.TearsService;
import jakarta.validation.Valid;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.LoginResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tebucks.dao.UserDao;
import com.techelevator.tebucks.model.LoginDto;
import com.techelevator.tebucks.model.RegisterUserDto;
import com.techelevator.tebucks.model.User;
import com.techelevator.tebucks.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller to authenticate users.
 */
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;
    private TearsService tearsService;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, TearsService tearsService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.tearsService = tearsService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        User user;
        try {
            user = userDao.getUserByUsername(loginDto.getUsername());
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect.");
        }

        return new LoginResponseDto(jwt, user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDto newUser) {
        try {
            if (userDao.getUserByUsername(newUser.getUsername()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists.");
            } else {
                userDao.createUser(newUser);

                // Register user in TEARS
                // Use the TearsService instance (assume it's @Autowired in this controller)
                tearsService.registerNewUser(newUser.getUsername(), newUser.getPassword());
                //TODO: The tears service needs to make a request to the post endpoint
                // to register a new tears account

                //from an instance of the tears service in this class, call that method
                // to register a tears account
            }
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
    }



}

