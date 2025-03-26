package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.repository.ProfileRepositoryDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class UserRepositoryTest extends JpaTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Artist testArtist;
    private Blog testBlog;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser = testDataSetup.createTestUser();
        testArtist = testDataSetup.createTestArtist();
        testDataSetup.createTestUserArtist(testUser, testArtist);
        testBlog = testDataSetup.createTestBlog(testUser);
    }

    @Test
    void findBySubject_正常系_存在するsubjectの場合() {
        // 実行
        Optional<User> result = userRepository.findBySubject(testUser.getSubject());

        // 検証
        assertThat(result).isPresent();
        assertThat(result.get().getDisplayName()).isEqualTo(testUser.getDisplayName());
    }

    @Test
    void findBySubject_正常系_存在しないsubjectの場合() {
        // 実行
        Optional<User> result = userRepository.findBySubject("non-existent-subject");

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    void updateProfile_正常系_全項目更新() {
        // 準備
        String newDisplayName = "Updated User";
        String newFilePath = "new-image.jpg";
        LocalDateTime updateTime = LocalDateTime.now();

        // 実行
        userRepository.updateProfile(
            testUser.getId(),
            newDisplayName,
            newFilePath,
            updateTime
        );
        entityManager.flush();
        entityManager.refresh(testUser);

        // 検証
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getDisplayName()).isEqualTo(newDisplayName);
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo(newFilePath);
    }

    @Test
    void updateProfile_正常系_画像パスがnullの場合() {
        // 準備
        String newDisplayName = "Updated User";
        LocalDateTime updateTime = LocalDateTime.now();

        // 実行
        userRepository.updateProfile(
            testUser.getId(),
            newDisplayName,
            null,
            updateTime
        );

        entityManager.flush();
        entityManager.refresh(testUser);


        // 検証
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getDisplayName()).isEqualTo(newDisplayName);
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo(testUser.getProfileImageUrl()); // 元の値が保持される
    }

    @Test
    void deleteUser_正常系() {
        // 実行
        userRepository.deleteUser(testUser.getId());

        entityManager.flush();
        entityManager.refresh(testUser);

        // 検証
        User deletedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(deletedUser.getEnabled()).isFalse();
    }

    @Test
    void updateUserProfile_正常系_全項目更新() {
        // 準備
        String newDisplayName = "Updated User";
        String newSelfIntroduction = "Updated Introduction";
        String newProfileImageUrl = "new-image.jpg";

        // 実行
        userRepository.updateUserProfile(
            testUser.getId(),
            newDisplayName,
            newSelfIntroduction,
            newProfileImageUrl
        );
        entityManager.flush();
        entityManager.refresh(testUser);

        // 検証
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getDisplayName()).isEqualTo(newDisplayName);
        assertThat(updatedUser.getSelfIntroduction()).isEqualTo(newSelfIntroduction);
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
    }

    @Test
    void updateUserProfile_正常系_一部項目のみ更新() {
        // 準備
        String newDisplayName = "Updated User";
        String originalSelfIntroduction = testUser.getSelfIntroduction();
        String originalProfileImageUrl = testUser.getProfileImageUrl();

        // 実行
        userRepository.updateUserProfile(
            testUser.getId(),
            newDisplayName,
            null,
            null
        );
        entityManager.flush();
        entityManager.refresh(testUser);

        // 検証
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getDisplayName()).isEqualTo(newDisplayName);
        assertThat(updatedUser.getSelfIntroduction()).isNull();
        assertThat(updatedUser.getProfileImageUrl()).isNull();
    }

    @Test
    void findUserProfileById_正常系() {
        // 実行
        ProfileRepositoryDto result = userRepository.findUserProfileById(testUser.getId());

        // 検証
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUser.getId());
        assertThat(result.getDisplayName()).isEqualTo(testUser.getDisplayName());
        assertThat(result.getProfileImageUrl()).isEqualTo(testUser.getProfileImageUrl());
        assertThat(result.getSelfIntroduction()).isEqualTo(testUser.getSelfIntroduction());
        assertThat(result.getFavoriteArtistList()).isEmpty();
        assertThat(result.getBlogList()).isEmpty();
    }

    @Test
    void findUserProfileById_正常系_存在しないユーザーの場合() {
        // 実行
        ProfileRepositoryDto result = userRepository.findUserProfileById(999L);

        // 検証
        assertThat(result).isNull();
    }

    @Test
    void searchUser_正常系_表示名での検索() {
        // 実行
        List<User> results = userRepository.searchUser("Test", 999L);

        // 検証
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDisplayName()).isEqualTo(testUser.getDisplayName());
    }

    @Test
    void searchUser_正常系_アーティスト名での検索() {
        // 実行
        List<User> results = userRepository.searchUser("Artist", 999L);

        // 検証
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDisplayName()).isEqualTo(testUser.getDisplayName());
    }

    @Test
    void searchUser_正常系_検索結果なし() {
        // 実行
        List<User> results = userRepository.searchUser("NonExistent", 999L);

        // 検証
        assertThat(results).isEmpty();
    }

    @Test
    void searchUser_正常系_複数件ヒット() {
        // 準備
        User anotherUser = User.builder()
            .displayName("Test Another")
            .subject("subject2")
            .enabled(true)
            .createdBy("System")
            .updatedBy("System")
            .build();
        userRepository.save(anotherUser);
        entityManager.flush();

        // 実行
        List<User> results = userRepository.searchUser("Test", 999L);

        // 検証
        assertThat(results).hasSize(2);
        assertThat(results).extracting("displayName")
            .containsExactlyInAnyOrder(testUser.getDisplayName(), "Test Another");
    }

    @Test
    void searchUser_正常系_キーワードが空文字の場合() {
        // 実行
        List<User> results = userRepository.searchUser("", 999L);

        // 検証
        assertThat(results).isEmpty();
    }

    @Test
    void searchUser_正常系_自分自身は除外() {
        // 実行
        List<User> results = userRepository.searchUser("Test", testUser.getId());

        // 検証
        assertThat(results).isEmpty();
    }
} 