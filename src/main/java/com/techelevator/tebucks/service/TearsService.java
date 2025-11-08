package com.techelevator.tebucks.service;

import com.techelevator.tebucks.model.TearsLoginResponeDto;
import com.techelevator.tebucks.model.Transfer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TearsService {
    private final String TEARS_BASE_URL = "https://tears.azurewebsites.net";
    private final String LOGIN_ENDPOINT = "/Login";
    private final String LOG_ENDPOINT = "/api/TxLog/";
    private RestClient restClient = RestClient.create(TEARS_BASE_URL);
    private String authToken = null;

    @Value("${tears.username}")
    private String tearsUsername;

    @Value("${tears.password}")
    private String tearsPassword;

    @PostConstruct                                       // -------------------------------------ADDED
    public void init() {
        try {
            System.out.println("Attempting automatic TEARS login from TearsService...");
            loginToTears(tearsUsername, tearsPassword);
            System.out.println("TEARS login successful!");
        } catch (Exception e) {
            System.err.println("TEARS login failed at startup: " + e.getMessage());
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public void ensureLoggedIn() {
        if (authToken == null) {
            loginToTears(tearsUsername, tearsPassword);
        }
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        // rebuild restClient with default headers
        restClient = RestClient.builder()
                .baseUrl(TEARS_BASE_URL)
                .defaultHeader("Authorization","Bearer " + authToken)
                .defaultHeader("Content-type", "application/json") //                 <---------------------------ADDED
                .build();
    }

    public void registerNewUser(String username, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        try {
            restClient.post()
                    .uri("/register")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();  // Registration returns void
            System.out.println("TEARS registration successful for user: " + username);
        } catch (RestClientResponseException e) {
            System.out.println("TEARS registration failed: " + e.getRawStatusCode() + " " + e.getResponseBodyAsString());
        }
    }

    public void loginToTears(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = TEARS_BASE_URL + LOGIN_ENDPOINT;

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            TearsLoginResponeDto response = restTemplate.postForObject(
                    url,
                    request,
                    TearsLoginResponeDto.class
            );

            if (response == null || response.getToken() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "TEARS login failed: Response was null or missing token");
            }

            this.authToken = response.getToken();
            System.out.println("Logged in to TEARS successfully. Token: " + this.authToken);

            setAuthToken(this.authToken);                    // --------------------------------------------------------------------------


        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace for debugging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "TEARS login failed: " + e.getMessage());
        }
    }



    public void logTransfer(Transfer transfer) {
        ensureLoggedIn();

        Map<String, Object> tearsPayload = new LinkedHashMap<>();                          //------------------------

        String usernameFrom = (transfer.getUserFrom() != null) ? transfer.getUserFrom().getUsername() : null;
        String  usernameTo = (transfer.getUserTo() != null) ? transfer.getUserTo().getUsername() : null;

        tearsPayload.put("description", "TEBucks transfer logged automatically");          //---------------------
        tearsPayload.put("username_from", usernameFrom);
        tearsPayload.put("username_to", usernameTo);
        tearsPayload.put("Amount", transfer.getAmount());                                    //-------------------


        try {
            restClient.post()
                    .uri(LOG_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)               //-------------------- was header
                    .body(tearsPayload)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("TEARS transfer logged: " + transfer.getAmount());

        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to log transfer to TEARS: " + e.getRawStatusCode() + " " + e.getResponseBodyAsString()
            );
        }
    }

    public boolean shouldLogTransfer(Transfer transfer, double senderBalance) {
        return transfer.getAmount() >= 1000 || transfer.getAmount() > senderBalance;
   }

}
