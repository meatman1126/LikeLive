package com.example.bookstore.config;


import com.example.bookstore.service.common.GoogleTokenVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final GoogleTokenVerifier tokenVerifier;

    public SecurityConfig(GoogleTokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/index"
                        ).permitAll()
                        // /api/public 配下は未認証のユーザもアクセス可能
                        .requestMatchers("/login/callback").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/oauth/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/**.ico").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new GoogleTokenAuthenticationFilter(tokenVerifier),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
