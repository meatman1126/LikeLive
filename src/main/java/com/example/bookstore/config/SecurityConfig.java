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
                        .logoutSuccessUrl("/top") // ログアウト成功時top画面に戻る
                        .invalidateHttpSession(true) // セッションの無効化
                        .deleteCookies("JSESSIONID")); // クッキーの削除

        return http.build();
    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // APIと静的リソースのアクセス制御
//                .authorizeHttpRequests(authorize -> authorize
//                        // 静的リソースやログインページは認証なしでアクセス可能
//                        .requestMatchers("/login", "/top", "/static/**", "/build/**", "/images/**").permitAll()
//                        // APIは認証が必要
//                        .requestMatchers("/api/**").authenticated()
//                        .anyRequest().permitAll())
//                // OAuth2ログイン設定
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")  // ログインページの設定
//                        .defaultSuccessUrl("/index.html")  // ログイン成功時のリダイレクト先（SPAのエントリーポイント）
//                        .permitAll())
//
//                // ログアウト設定
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/index.html")  // ログアウト成功後はSPAのエントリーポイントにリダイレクト
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID"));
//        // CSRFを無効化（必要に応じて設定）
////                .csrf().disable();
//
//        return http.build();
//    }

}
