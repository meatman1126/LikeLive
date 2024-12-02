package com.example.bookstore.service;

import com.example.bookstore.dto.GoogleUserInfo;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
     */
    public GoogleUserInfo getUserInfo(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String GOOGLE_USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(GOOGLE_USERINFO_ENDPOINT, HttpMethod.GET, entity, GoogleUserInfo.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new Exception("Failed to fetch user info from Google API");
        }
    }
}
