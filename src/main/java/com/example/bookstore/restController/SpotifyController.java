package com.example.bookstore.restController;

import com.example.bookstore.service.SpotifyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/get-token")
    public String getToken() throws JsonProcessingException {
        return spotifyService.getAccessToken();
    }
}