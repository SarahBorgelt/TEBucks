package com.techelevator.tebucks.service;

import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TearsService {
    private final RestTemplate restTemplate;
    private final JdbcTearsAccountDao tearsAccountDao;


    public TearsService(RestTemplate restTemplate, JdbcTearsAccountDao tearsAccountDao) {
        this.restTemplate = restTemplate;
        this.tearsAccountDao = tearsAccountDao;
    }

    @param
}
