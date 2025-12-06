package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.repository.FollowRepositoryDto;
import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class FollowRepositoryTest extends JpaTestBase {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser1;
    private User testUser2;
    private User testUser3;
    private Follow follow1;
    private Follow follow2;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser1 = testDataSetup.createTestUser();
        testUser2 = testDataSetup.createTestUser();
        testUser3 = testDataSetup.createTestUser();

        // フォロー関係の作成
        follow1 = testDataSetup.createTestFollow(testUser1, testUser2);
        follow2 = testDataSetup.createTestFollow(testUser3, testUser2);
    }

    @Test
    @DisplayName("findFollowers_正常系_フォロワーが存在する場合")
    void findFollowers_正常系_フォロワーが存在する場合() {
        // 実行
        List<User> result = followRepository.findFollowers(testUser2.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id")
            .containsExactlyInAnyOrder(testUser1.getId(), testUser3.getId());
    }

    @Test
    @DisplayName("findFollowers_正常系_フォロワーが存在しない場合")
    void findFollowers_正常系_フォロワーが存在しない場合() {
        // 実行
        List<User> result = followRepository.findFollowers(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findFollowedUsersInfo_正常系_フォローしているユーザが存在する場合")
    void findFollowedUsersInfo_正常系_フォローしているユーザが存在する場合() {
        // 実行
        List<FollowRepositoryDto> result = followRepository.findFollowedUsersInfo(testUser1.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(testUser2.getId());
        assertThat(result.get(0).getIsFollowing()).isTrue();
    }

    @Test
    @DisplayName("findFollowedUsersInfo_正常系_フォローしているユーザが存在しない場合")
    void findFollowedUsersInfo_正常系_フォローしているユーザが存在しない場合() {
        // 実行
        List<FollowRepositoryDto> result = followRepository.findFollowedUsersInfo(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findFollowersInfo_正常系_フォロワーが存在する場合")
    void findFollowersInfo_正常系_フォロワーが存在する場合() {
        // 実行
        List<FollowRepositoryDto> result = followRepository.findFollowersInfo(testUser2.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting("user.id")
            .containsExactlyInAnyOrder(testUser1.getId(), testUser3.getId());
        assertThat(result).extracting("isFollowing")
            .containsOnly(false); // testUser2は誰もフォローしていないため
    }

    @Test
    @DisplayName("findFollowersInfo_正常系_フォロワーが存在しない場合")
    void findFollowersInfo_正常系_フォロワーが存在しない場合() {
        // 実行
        List<FollowRepositoryDto> result = followRepository.findFollowersInfo(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("isFollowing_正常系_フォローしている場合")
    void isFollowing_正常系_フォローしている場合() {
        // 実行
        Boolean result = followRepository.isFollowing(testUser1.getId(), testUser2.getId());

        // 検証
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isFollowing_正常系_フォローしていない場合")
    void isFollowing_正常系_フォローしていない場合() {
        // 実行
        Boolean result = followRepository.isFollowing(testUser1.getId(), testUser3.getId());

        // 検証
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("countFollowedUsers_正常系_フォローしているユーザが存在する場合")
    void countFollowedUsers_正常系_フォローしているユーザが存在する場合() {
        // 実行
        Long result = followRepository.countFollowedUsers(testUser1.getId());

        // 検証
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("countFollowedUsers_正常系_フォローしているユーザが存在しない場合")
    void countFollowedUsers_正常系_フォローしているユーザが存在しない場合() {
        // 実行
        Long result = followRepository.countFollowedUsers(999L);

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("countFollowers_正常系_フォロワーが存在する場合")
    void countFollowers_正常系_フォロワーが存在する場合() {
        // 実行
        Long result = followRepository.countFollowers(testUser2.getId());

        // 検証
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("countFollowers_正常系_フォロワーが存在しない場合")
    void countFollowers_正常系_フォロワーが存在しない場合() {
        // 実行
        Long result = followRepository.countFollowers(999L);

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("unfollow_正常系_フォロー関係が存在する場合")
    void unfollow_正常系_フォロー関係が存在する場合() {
        // 実行
        followRepository.unfollow(testUser1.getId(), testUser2.getId());
        entityManager.flush();

        // 検証
        Boolean isFollowing = followRepository.isFollowing(testUser1.getId(), testUser2.getId());
        assertThat(isFollowing).isFalse();
    }

    @Test
    @DisplayName("unfollow_正常系_フォロー関係が存在しない場合")
    void unfollow_正常系_フォロー関係が存在しない場合() {
        // 実行
        followRepository.unfollow(999L, 888L);
        entityManager.flush();

        // 検証
        Boolean isFollowing = followRepository.isFollowing(999L, 888L);
        assertThat(isFollowing).isFalse();
    }
} 