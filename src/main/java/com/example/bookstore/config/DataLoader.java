package com.example.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        if (userRepository.count() == 0) { // データベースが空の場合のみデフォルトユーザを追加
//            User newUser = User.builder().enabled(true).build();
//            userRepository.save(newUser);
//        }
    }
}
