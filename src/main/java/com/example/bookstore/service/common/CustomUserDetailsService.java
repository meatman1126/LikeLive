package com.example.bookstore.service.common;

import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        com.example.bookstore.entity.User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }

        return null;
    }
}
