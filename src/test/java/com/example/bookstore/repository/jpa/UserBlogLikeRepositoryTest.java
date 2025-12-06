package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserBlogLike;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class UserBlogLikeRepositoryTest extends JpaTestBase {

    @Autowired
    private UserBlogLikeRepository userBlogLikeRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser1;
    private User testUser2;
    private Blog testBlog1;
    private Blog testBlog2;
    private UserBlogLike userBlogLike1;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser1 = testDataSetup.createTestUser();
        testUser2 = testDataSetup.createTestUser();
        testBlog1 = testDataSetup.createTestBlog(testUser1);
        testBlog2 = testDataSetup.createTestBlog(testUser2);

        // いいねの作成
        userBlogLike1 = testDataSetup.createTestUserBlogLike(testUser1, testBlog1);
    }

    @Test @DisplayName("isLikeBlog_正常系_いいねが存在する場合")
    void isLikeBlog_正常系_いいねが存在する場合() {
        // 実行
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), testBlog1.getId());

        // 検証
        assertThat(isLiked).isTrue();
    }

    @Test @DisplayName("isLikeBlog_正常系_いいねが存在しない場合")
    void isLikeBlog_正常系_いいねが存在しない場合() {
        // 実行
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), testBlog2.getId());

        // 検証
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("isLikeBlog_異常系_ユーザが存在しない場合")
    void isLikeBlog_異常系_ユーザが存在しない場合() {
        // 実行
        boolean isLiked = userBlogLikeRepository.isLikeBlog(999L, testBlog1.getId());

        // 検証
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("isLikeBlog_異常系_ブログが存在しない場合")
    void isLikeBlog_異常系_ブログが存在しない場合() {
        // 実行
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), 999L);

        // 検証
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("clearLikeBlog_正常系_いいねが存在する場合")
    void clearLikeBlog_正常系_いいねが存在する場合() {
        // 実行
        userBlogLikeRepository.clearLikeBlog(testUser1.getId(), testBlog1.getId());

        // 検証
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), testBlog1.getId());
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("clearLikeBlog_正常系_いいねが存在しない場合")
    void clearLikeBlog_正常系_いいねが存在しない場合() {
        // 実行
        userBlogLikeRepository.clearLikeBlog(testUser1.getId(), testBlog2.getId());

        // 検証
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), testBlog2.getId());
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("clearLikeBlog_異常系_ユーザが存在しない場合")
    void clearLikeBlog_異常系_ユーザが存在しない場合() {
        // 実行
        userBlogLikeRepository.clearLikeBlog(999L, testBlog1.getId());

        // 検証
        boolean isLiked = userBlogLikeRepository.isLikeBlog(999L, testBlog1.getId());
        assertThat(isLiked).isFalse();
    }

    @Test @DisplayName("clearLikeBlog_異常系_ブログが存在しない場合")
    void clearLikeBlog_異常系_ブログが存在しない場合() {
        // 実行
        userBlogLikeRepository.clearLikeBlog(testUser1.getId(), 999L);

        // 検証
        boolean isLiked = userBlogLikeRepository.isLikeBlog(testUser1.getId(), 999L);
        assertThat(isLiked).isFalse();
    }
}