package com.example.bookstore.config;

import com.example.bookstore.service.common.GoogleTokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class GoogleTokenAuthenticationFilter extends OncePerRequestFilter {

    private final GoogleTokenVerifier tokenVerifier;

    public GoogleTokenAuthenticationFilter(GoogleTokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // トークンの検証とsubjectの抽出
            String subject = tokenVerifier.verifyToken(token);
            if (subject != null) {
                // subjectを含むAuthenticationオブジェクトを作成
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        subject, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                // SecurityContextに保存
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
