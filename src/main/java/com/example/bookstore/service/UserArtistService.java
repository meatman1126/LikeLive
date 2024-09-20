package com.example.bookstore.service;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.repository.jpa.UserArtistRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserArtistService {

    @Autowired
    private UserArtistRepository userArtistRepository;

    @Autowired
    private UserUtilService userUtilService;

    /**
     * 指定されたユーザの好きなアーティスト一覧を取得します。
     *
     * @param userId ユーザID
     * @return アーティスト一覧
     */
    public List<Artist> getFavoriteArtistsByUserId(Long userId) {
        return userArtistRepository.findFavoriteArtistsByUserId(userId);
    }

    /**
     * ログイン中のユーザの好きなアーティスト一覧を取得します。
     *
     * @return アーティスト一覧
     */
    public List<Artist> getCurrentUserFavorite() {
        return getFavoriteArtistsByUserId(userUtilService.getCurrentUser().getId());
    }

    /**
     * 指定されたアーティストを好きなユーザ一覧を取得します。
     *
     * @param artistId アーティストID
     * @return ユーザ一覧
     */
    public List<User> getUsersByFavoriteArtistId(String artistId) {
        return userArtistRepository.findUsersByFavoriteArtistId(artistId);
    }

    /**
     * 指定されたユーザと好きなアーティストが共通している他のユーザをアーティスト情報とともに取得します。
     *
     * @param userId ユーザID
     * @return 同じアーティストが好きな他のユーザとアーティストのリスト
     */
    public List<UserArtist> getUserSameFavorite(Long userId) {
        return userArtistRepository.findUserSameFavorite(userId);
    }

    /**
     * ログイン中のユーザと好きなアーティストが共通している他のユーザをアーティスト情報とともに取得します。
     *
     * @return 同じアーティストが好きな他のユーザとアーティストのリスト
     */
    public List<UserArtist> getUserSameFavoriteWithCurrentUser() {
        return getUserSameFavorite(userUtilService.getCurrentUser().getId());
    }
    

}
