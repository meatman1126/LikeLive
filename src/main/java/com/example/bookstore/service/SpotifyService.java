package com.example.bookstore.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    private final String tokenUrl = "https://accounts.spotify.com/api/token";
    @Value("${spotify.client-id}")
    private String clientId;
    @Value("${spotify.client-secret}")
    private String clientSecret;

    public String getAccessToken() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        logger.debug("Response: {}", response.getBody());

        // Deserialize response body to SpotifyTokenResponse
        SpotifyTokenResponse tokenResponse = new ObjectMapper().readValue(response.getBody(), SpotifyTokenResponse.class);

        if (tokenResponse != null) {
            return tokenResponse.getAccessToken();
        } else {
            throw new RuntimeException("Failed to get access token");
        }
    }

    @Data
    static class SpotifyTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private int expiresIn;
    }
}