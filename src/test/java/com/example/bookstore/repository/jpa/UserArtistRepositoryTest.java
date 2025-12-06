package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.repository.DashboardUserRepositoryDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class UserArtistRepositoryTest extends JpaTestBase {

    @Autowired
    private UserArtistRepository userArtistRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser1;
    private User testUser2;
    private Artist testArtist1;
    private Artist testArtist2;
    private UserArtist userArtist1;
    private UserArtist userArtist2;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser1 = testDataSetup.createTestUser();
        testUser2 = testDataSetup.createTestUser();
        testArtist1 = testDataSetup.createTestArtist();
        testArtist2 = testDataSetup.createTestArtist();

        // UserArtistの関連付け
        userArtist1 = testDataSetup.createTestUserArtist(testUser1, testArtist1);
        userArtist2 = testDataSetup.createTestUserArtist(testUser1, testArtist2);
    }

    @Test @DisplayName("findFavoriteArtistsByUserId_正常系_お気に入りアーティストが存在する場合")
    void findFavoriteArtistsByUserId_正常系_お気に入りアーティストが存在する場合() {
        // 実行
        List<Artist> favoriteArtists = userArtistRepository.findFavoriteArtistsByUserId(testUser1.getId());

        // 検証
        assertThat(favoriteArtists).hasSize(2);
        assertThat(favoriteArtists).contains(testArtist1, testArtist2);
    }

    @Test @DisplayName("findFavoriteArtistsByUserId_正常系_お気に入りアーティストが存在しない場合")
    void findFavoriteArtistsByUserId_正常系_お気に入りアーティストが存在しない場合() {
        // 実行
        List<Artist> favoriteArtists = userArtistRepository.findFavoriteArtistsByUserId(testUser2.getId());

        // 検証
        assertThat(favoriteArtists).isEmpty();
    }

    @Test @DisplayName("findUsersByFavoriteArtistId_正常系_アーティストを好きなユーザが存在する場合")
    void findUsersByFavoriteArtistId_正常系_アーティストを好きなユーザが存在する場合() {
        // 準備
        testDataSetup.createTestUserArtist(testUser2, testArtist1);

        // 実行
        List<User> users = userArtistRepository.findUsersByFavoriteArtistId(testArtist1.getId());

        // 検証
        assertThat(users).hasSize(2);
        assertThat(users).contains(testUser1, testUser2);
    }

    @Test @DisplayName("findRecommendedUsersWithFollowStatus_正常系_フォロー済みユーザが存在する場合")
    void findRecommendedUsersWithFollowStatus_正常系_フォロー済みユーザが存在する場合() {
        // 準備
        testDataSetup.createTestUserArtist(testUser2, testArtist1);
        testDataSetup.createTestFollow(testUser1, testUser2);

        // 実行
        List<DashboardUserRepositoryDto> recommendedUsers = userArtistRepository.findRecommendedUsersWithFollowStatus(
                Arrays.asList(testArtist1.getId(), testArtist2.getId()), testUser1.getId());

        // 検証
        assertThat(recommendedUsers).hasSize(1);
        assertThat(recommendedUsers.get(0).getUser()).isEqualTo(testUser2);
        assertThat(recommendedUsers.get(0).isFollowing()).isTrue();
    }

    @Test @DisplayName("findUserSameFavorite_正常系_共通のアーティストを好きなユーザが存在する場合")
    void findUserSameFavorite_正常系_共通のアーティストを好きなユーザが存在する場合() {
        // 準備
        testDataSetup.createTestUserArtist(testUser2, testArtist1);

        // 実行
        List<UserArtist> sameFavorites = userArtistRepository.findUserSameFavorite(testUser1.getId());

        // 検証
        assertThat(sameFavorites).hasSize(1);
        assertThat(sameFavorites.get(0).getUser()).isEqualTo(testUser2);
        assertThat(sameFavorites.get(0).getArtist()).isEqualTo(testArtist1);
    }

    @Test @DisplayName("existsByUserIdAndArtistId_正常系_リレーションが存在する場合")
    void existsByUserIdAndArtistId_正常系_リレーションが存在する場合() {
        // 実行
        boolean exists = userArtistRepository.existsByUserIdAndArtistId(testUser1.getId(), testArtist1.getId());

        // 検証
        assertThat(exists).isTrue();
    }

    @Test @DisplayName("existsByUserIdAndArtistId_正常系_リレーションが存在しない場合")
    void existsByUserIdAndArtistId_正常系_リレーションが存在しない場合() {
        // 実行
        boolean exists = userArtistRepository.existsByUserIdAndArtistId(testUser2.getId(), testArtist1.getId());

        // 検証
        assertThat(exists).isFalse();
    }

    @Test @DisplayName("deleteAllByUserId_正常系_ユーザの全てのリレーションが削除されること")
    void deleteAllByUserId_正常系_ユーザの全てのリレーションが削除されること() {
        // 実行
        userArtistRepository.deleteAllByUserId(testUser1.getId());

        // 検証
        List<Artist> favoriteArtists = userArtistRepository.findFavoriteArtistsByUserId(testUser1.getId());
        assertThat(favoriteArtists).isEmpty();
    }
}