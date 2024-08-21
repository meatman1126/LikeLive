package com.example.bookstore.config;

//import com.example.bookstore.service.common.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
//    private CustomUserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/top")
                        .defaultSuccessUrl("/login/oauth")
                        .permitAll())
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .loginPage("/login")
                        .defaultSuccessUrl("/login/success")
                        //ログイン失敗時のURL
                        .failureUrl("/login/error")
                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/top", "/build/**", "/images/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"));

        return http.build();
    }

}
