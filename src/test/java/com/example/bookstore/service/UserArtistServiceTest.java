package com.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.view.DashboardUserViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.repository.jpa.UserArtistRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class UserArtistServiceTest extends SpringBootTestBase {

    @Autowired
    private UserArtistService userArtistService;

    @Autowired
    private UserArtistRepository userArtistRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User currentUser;
    private User otherUser;
    private Artist testArtist1;
    private Artist testArtist2;
    private UserArtist userArtist1;
    private UserArtist userArtist2;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        currentUser = testDataSetup.createUser("test-user", "Test User");
        otherUser = testDataSetup.createUser("other-user", "Other User");

        // 認証情報の設定
        testDataSetup.setupAuthentication(currentUser);

        // アーティストの準備
        testArtist1 = testDataSetup.createTestArtist();
        testArtist2 = testDataSetup.createTestArtist();

        // ユーザとアーティストの関連付け
        userArtist1 = testDataSetup.createTestUserArtist(currentUser, testArtist1);
        userArtist2 = testDataSetup.createTestUserArtist(currentUser, testArtist2);
    }

    @Test
    void getFavoriteArtistsByUserId_正常系() {
        // ユーザの好きなアーティスト一覧を取得
        List<Artist> artists = userArtistService.getFavoriteArtistsByUserId(currentUser.getId());

        // 検証
        assertThat(artists).hasSize(2);
        assertThat(artists).containsExactlyInAnyOrder(testArtist1, testArtist2);
    }

    @Test
    void getCurrentUserFavorite_正常系() {
        // ログインユーザの好きなアーティスト一覧を取得
        List<Artist> artists = userArtistService.getCurrentUserFavorite();

        // 検証
        assertThat(artists).hasSize(2);
        assertThat(artists).containsExactlyInAnyOrder(testArtist1, testArtist2);
    }

    @Test
    void getUsersByFavoriteArtistId_正常系() {
        // アーティストを好きなユーザ一覧を取得
        List<User> users = userArtistService.getUsersByFavoriteArtistId(testArtist1.getId());

        // 検証
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(currentUser);
    }

    @Test
    void getUserSameFavorite_正常系() {
        // 他のユーザも同じアーティストを好きにする
        UserArtist otherUserArtist = testDataSetup.createTestUserArtist(otherUser, testArtist1);
        entityManager.flush();
        entityManager.refresh(otherUserArtist);

        // 同じアーティストが好きなユーザを取得
        List<UserArtist> userArtists = userArtistService.getUserSameFavorite(currentUser.getId());

        // 検証
        assertThat(userArtists).hasSize(1);
        assertThat(userArtists.get(0)).isEqualTo(otherUserArtist);
    }

    @Test
    void getUserSameFavoriteWithCurrentUser_正常系() {
        // 他のユーザも同じアーティストを好きにする
        UserArtist otherUserArtist = testDataSetup.createTestUserArtist(otherUser, testArtist1);
        entityManager.flush();
        entityManager.refresh(otherUserArtist);

        // ログインユーザと同じアーティストが好きなユーザを取得
        List<UserArtist> userArtists = userArtistService.getUserSameFavoriteWithCurrentUser();

        // 検証
        assertThat(userArtists).hasSize(1);
        assertThat(userArtists.get(0)).isEqualTo(otherUserArtist);
    }

    @Test
    void getRecommendedUsers_正常系() {
        // 他のユーザも同じアーティストを好きにする
        UserArtist otherUserArtist = testDataSetup.createTestUserArtist(otherUser, testArtist1);
        entityManager.flush();
        entityManager.refresh(otherUserArtist);

        // おすすめユーザリストを取得
        List<DashboardUserViewDto> recommendedUsers = userArtistService.getRecommendedUsers(currentUser.getId());

        // 検証
        assertThat(recommendedUsers).hasSize(1);
        assertThat(recommendedUsers.get(0).getUser()).isEqualTo(otherUser);
    }

    @Test
    void getRecommendedUsers_フォロー状況込み_正常系() {
        // 他のユーザも同じアーティストを好きにする
        UserArtist otherUserArtist = testDataSetup.createTestUserArtist(otherUser, testArtist1);
        
        // フォロー関係を作成
        testDataSetup.createTestFollow(currentUser, otherUser);
        
        entityManager.flush();
        entityManager.refresh(otherUserArtist);

        // おすすめユーザリストを取得
        List<DashboardUserViewDto> recommendedUsers = userArtistService.getRecommendedUsers(currentUser.getId());

        // 検証
        assertThat(recommendedUsers).hasSize(1);
        assertThat(recommendedUsers.get(0).getUser()).isEqualTo(otherUser);
        assertThat(recommendedUsers.get(0).isFollowing()).isTrue();
    }
} 