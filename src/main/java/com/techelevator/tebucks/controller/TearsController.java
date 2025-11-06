package com.techelevator.tebucks.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class TearsController {
    @PostMapping(path = "https://tears.azurewebsites.net/login")
    public login(@Valid @RequestBody String username, String password){

    }

}
