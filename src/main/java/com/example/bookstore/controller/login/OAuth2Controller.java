package com.example.bookstore.controller.login;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2Controller(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/authorization/{clientId}")
    public void redirectToGoogleAuth(HttpServletResponse response, @PathVariable String clientId) throws IOException {
        // クライアントIDに基づいてGoogleのOAuth認証ページにリダイレクトします
        response.sendRedirect("/oauth2/authorization/google");
    }
}