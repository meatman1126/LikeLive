package com.example.bookstore.restController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RestOAuthController {

    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.clientSecret}")
    private String clientSecret;

    @PostMapping("/api/oauth/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        // Googleのトークンサーバーにリクエストを送る
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", "http://localhost:3000/login/callback");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", request, String.class);

        // アクセストークンを解析して、ユーザー情報を取得する処理
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/api/oauth/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        final String TOKEN_REFRESH_URL = "https://oauth2.googleapis.com/token";
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing refresh token"));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> params = new HashMap<>();
            params.put("refresh_token", refreshToken);
            params.put("grant_type", "refresh_token");
            params.put("client_id", clientId);  // クライアントIDを設定
            params.put("client_secret", clientSecret); // クライアントシークレットを設定

            Map<String, Object> response = restTemplate.postForObject(TOKEN_REFRESH_URL, params, Map.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
