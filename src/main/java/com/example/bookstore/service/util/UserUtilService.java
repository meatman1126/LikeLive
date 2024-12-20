package com.example.bookstore.service.util;

import com.example.bookstore.entity.User;
import com.example.bookstore.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserUtilService {

    @Autowired
    UserRepository userRepository;

//    /**
//     * ログイン中のユーザ情報を取得します。
//     *
//     * @return ログイン中のユーザ情報
//     */
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
//            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
//            return userRepository.findBySubject(oidcUser.getAttribute("sub")).orElseThrow(
//                    () -> new UserNotFoundException("Current user not found")
//            );
//        }
//        throw new RuntimeException();
//    }

    /**
     * ログイン中のユーザ情報を取得します。
     *
     * @return ログイン中のユーザ情報
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof String subject) {
                // トークンにsubjectが文字列で含まれている場合（カスタムフィルター等の利用時）
                return userRepository.findBySubject(subject).orElse(null);
            }
        }
        throw new RuntimeException("User is not authenticated");
    }


    /**
     * ログイン中のユーザのIDを文字列型で取得します。
     *
     * @return ログイン中のユーザID
     */
    public String getCurrentUserId() {
        return getCurrentUser().getId().toString();
    }

}
