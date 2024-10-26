package com.example.bookstore.restController;

import com.example.bookstore.dto.view.DashboardUserViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.service.UserArtistService;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Restユーザアーティストコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestUserArtistController {

    /**
     * ユーザアーティストサービス
     */
    @Autowired
    private UserArtistService userArtistService;

    @Autowired
    private UserUtilService userUtilService;

    /**
     * ログイン中のユーザの好きなアーティスト一覧を取得します。
     *
     * @return アーティスト一覧
     */
    @GetMapping("/user/my-artists")
    public ResponseEntity<List<Artist>> getCurrentUserFavorite() {
        List<Artist> favoriteArtists = userArtistService.getCurrentUserFavorite();
        return ResponseEntity.ok(favoriteArtists);
    }

    /**
     * 指定されたアーティストを好きなユーザ一覧を取得します。
     *
     * @param artistId アーティストID
     * @return ユーザ一覧
     */
    @GetMapping("/artist/{artistId}/users")
    public ResponseEntity<List<User>> getUsersByFavoriteArtistId(@PathVariable String artistId) {
        List<User> users = userArtistService.getUsersByFavoriteArtistId(artistId);
        return ResponseEntity.ok(users);
    }

    /**
     * ログイン中のユーザと好きなアーティストが共通している他のユーザ一覧を取得します。
     *
     * @return 同じアーティストが好きな他のユーザとアーティストのリスト
     */
    @GetMapping("/user/similar-users")
    public ResponseEntity<List<UserArtist>> getUserSameFavoriteWithCurrentUser() {
        List<UserArtist> userArtistRelations = userArtistService.getUserSameFavoriteWithCurrentUser();
        return ResponseEntity.ok(userArtistRelations);
    }

    /**
     * ログインユーザに対するおすすめユーザリストを取得します。
     *
     * @return おすすめユーザリスト
     */
    @GetMapping("/user/recommended-users")
    public ResponseEntity<List<DashboardUserViewDto>> getRecommendedUsers() {
        List<DashboardUserViewDto> recommendedUsers = userArtistService.getRecommendedUsers(userUtilService.getCurrentUser().getId());

        return ResponseEntity.ok(recommendedUsers);
    }
}