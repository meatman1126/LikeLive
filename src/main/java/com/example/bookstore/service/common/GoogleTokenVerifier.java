package com.example.bookstore.service.common;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Google OAuth 2.0 トークン検証サービス
 * 
 * 指定されたトークンを検証し、GoogleのユーザID（sub）を返す
 */
@Service
public class GoogleTokenVerifier {

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";

    /**
     * トークンを検証し、GoogleのユーザID（sub）を返すメソッド
     *
     * @param token Google OAuth アクセストークン
     * @return GoogleのユーザID（sub） or null（無効なトークンの場合）
     */
    public String verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = GOOGLE_TOKEN_URL + token;

            // Googleのトークン検証サーバへリクエストを送信
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 検証成功時、GoogleのユーザID（sub）を返す
            if (response != null && response.containsKey("sub")) {
                return response.get("sub").toString();
            } else {
                return null; // "sub"が含まれない場合
            }

        } catch (HttpClientErrorException e) {
            // トークンが無効な場合、例外が発生
            return null;
        } catch (Exception e) {
            // その他の例外発生時にもnullを返す
            e.printStackTrace();
            return null;
        }
    }
}
