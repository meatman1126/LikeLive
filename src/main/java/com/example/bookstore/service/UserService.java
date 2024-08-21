package com.example.bookstore.service;

import com.example.bookstore.dto.form.UserRegistrationForm;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import com.example.bookstore.repository.ArtistRepository;
import com.example.bookstore.repository.UserArtistRepository;
import com.example.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistService artistService;

    @Autowired
    UserArtistRepository userArtistRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            return userRepository.findBySubject(oidcUser.getAttribute("sub"));
        }

        return null;
    }

    /**
     * ユーザが登録済みかどうかを判定します。
     *
     * @param user Idpにより提供されるユーザ情報
     * @return subjectに紐づくユーザが登録されている場合True, 未登録の場合false
     */
    public Boolean isRegistered(OidcUser user) {
        return userRepository.findBySubject(user.getAttribute("sub")) != null;
    }

    /**
     * ユーザを新規登録します。
     *
     * @param input Idpに紐づくユーザ情報
     * @return 登録されたユーザ情報
     */
    public User register(OidcUser input) {
        User user = User.builder().displayName(input.getAttribute("given_name")).subject(input.getAttribute("sub")).enabled(true).build();
        return userRepository.save(user);
    }


    @Transactional
    public User update(UserRegistrationForm input) {
        // ユーザ情報の更新
        User currentUser = getCurrentUser();
        userRepository.updateDisplayName(currentUser.getId(), input.getUserName());

        User updatedUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        // アーティスト情報、ユーザ、アーティストリレーション情報の登録
        for (Artist artist : input.getArtistList()) {
//            artistRepository.save(artist);
            artistService.saveArtist(artist);
            UserArtist userArtist = UserArtist.builder().id(new UserArtistId()).user(updatedUser).artist(artist).favorite(false).build();
            userArtistRepository.save(userArtist);
        }
        return updatedUser;
    }
}
