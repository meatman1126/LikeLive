package com.example.bookstore.restController;

import com.example.bookstore.service.SpotifyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/spotify/get-token")
    public ResponseEntity<Map<String, String>> getToken() throws JsonProcessingException {
        System.out.println(LocalDateTime.now());
        return ResponseEntity.ok(spotifyService.getAccessToken());
    }
}