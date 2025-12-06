package com.example.bookstore.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bookstore.dto.GoogleUserInfo;

/**
 * GoogleAPIを呼び出すためのサービスクラスです。
 */
@Service
public class GoogleService {

    /**
     * アクセストークンに紐づくGoogleユーザ情報を取得します。
     *
     * @param accessToken アクセストークン
     * @return Googleユーザ情報
     * @throws BadCredentialsException アクセストークンが無効な場合
     */
    public GoogleUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String GOOGLE_USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(GOOGLE_USERINFO_ENDPOINT, HttpMethod.GET, entity, GoogleUserInfo.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new BadCredentialsException("Failed to fetch user info from Google API");
        }
    }
}
