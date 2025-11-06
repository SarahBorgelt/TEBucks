package com.techelevator.tebucks.service;

import com.techelevator.tebucks.model.Transfer;
import org.jboss.logging.BasicLogger;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class TearsService {
    private final String TEARS_BASE_URL = "https://tears.azurewebsites.net";
    private final String LOGIN_ENDPOINT = "/login";
    private final String LOG_ENDPOINT = "/api/TxLog/";
    private RestClient restClient = RestClient.create(TEARS_BASE_URL);
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        // rebuild restClient with default headers
        restClient = RestClient.builder()
                .baseUrl(TEARS_BASE_URL)
                .defaultHeader("Authorization","Bearer " + authToken)
                .build();
    }
   //  * Create a new reservation in the hotel reservation system
    public Transfer addTransfer(Transfer newTransfer) {
        Transfer returnedTransfer = null;
        try{
            restClient.post().uri(LOG_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                    .body(newTransfer)
                    .retrieve()
                    .body(Transfer.class);

        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } return returnedTransfer;
    }

    public boolean updateTransfer(Transfer updatedTransfer){
        boolean success = false;
        try{
            restClient.put()
                    .uri(LOG_ENDPOINT + updatedTransfer.getTransferId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updatedTransfer)
                    .retrieve()
                    .toBodilessEntity();

            success = true;
        } catch(RestClientResponseException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return success;
    }


}
