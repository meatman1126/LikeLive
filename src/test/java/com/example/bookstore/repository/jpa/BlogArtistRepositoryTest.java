package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.BlogArtist;
import com.example.bookstore.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class BlogArtistRepositoryTest extends JpaTestBase {

    @Autowired
    private BlogArtistRepository blogArtistRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Blog testBlog;
    private Artist testArtist1;
    private Artist testArtist2;
    private BlogArtist blogArtist1;
    private BlogArtist blogArtist2;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser = testDataSetup.createTestUser();
        testBlog = testDataSetup.createTestBlog(testUser);
        testArtist1 = testDataSetup.createTestArtist();
        testArtist2 = testDataSetup.createTestArtist();

        // ブログとアーティストの関連付け
        blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);
        blogArtist2 = testDataSetup.createTestBlogArtist(testBlog, testArtist2);
    }

    @Test @DisplayName("findByArtistId_正常系_アーティストに関連するブログが存在する場合")
    void findByArtistId_正常系_アーティストに関連するブログが存在する場合() {
        // 実行
        List<BlogArtist> result = blogArtistRepository.findByArtistId(testArtist1.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBlog().getId()).isEqualTo(testBlog.getId());
        assertThat(result.get(0).getArtist().getId()).isEqualTo(testArtist1.getId());
    }

    @Test @DisplayName("findByArtistId_正常系_アーティストに関連するブログが存在しない場合")
    void findByArtistId_正常系_アーティストに関連するブログが存在しない場合() {
        // 準備
        Artist newArtist = testDataSetup.createTestArtist();

        // 実行
        List<BlogArtist> result = blogArtistRepository.findByArtistId(newArtist.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findByBlogId_正常系_ブログに関連するアーティストが存在する場合")
    void findByBlogId_正常系_ブログに関連するアーティストが存在する場合() {
        // 実行
        List<BlogArtist> result = blogArtistRepository.findByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ba -> ba.getArtist().getId()).contains(testArtist1.getId(), testArtist2.getId());
    }

    @Test @DisplayName("findByBlogId_正常系_ブログに関連するアーティストが存在しない場合")
    void findByBlogId_正常系_ブログに関連するアーティストが存在しない場合() {
        // 準備
        Blog newBlog = testDataSetup.createTestBlog(testUser);

        // 実行
        List<BlogArtist> result = blogArtistRepository.findByBlogId(newBlog.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findArtistsByBlogId_正常系_ブログに関連するアーティストが存在する場合")
    void findArtistsByBlogId_正常系_ブログに関連するアーティストが存在する場合() {
        // 実行
        List<Artist> result = blogArtistRepository.findArtistsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Artist::getId).contains(testArtist1.getId(), testArtist2.getId());
    }

    @Test @DisplayName("findArtistsByBlogId_正常系_ブログに関連するアーティストが存在しない場合")
    void findArtistsByBlogId_正常系_ブログに関連するアーティストが存在しない場合() {
        // 準備
        Blog newBlog = testDataSetup.createTestBlog(testUser);

        // 実行
        List<Artist> result = blogArtistRepository.findArtistsByBlogId(newBlog.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("deleteByBlogIdAndArtistId_正常系_ブログとアーティストの関連が存在する場合")
    void deleteByBlogIdAndArtistId_正常系_ブログとアーティストの関連が存在する場合() {
        // 実行
        blogArtistRepository.deleteByBlogIdAndArtistId(testBlog.getId(), testArtist1.getId());
        entityManager.flush();

        // 検証
        List<BlogArtist> result = blogArtistRepository.findByBlogId(testBlog.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getArtist().getId()).isEqualTo(testArtist2.getId());
    }

    @Test @DisplayName("deleteByBlogIdAndArtistId_正常系_ブログとアーティストの関連が存在しない場合")
    void deleteByBlogIdAndArtistId_正常系_ブログとアーティストの関連が存在しない場合() {
        // 準備
        Artist newArtist = testDataSetup.createTestArtist();

        // 実行
        blogArtistRepository.deleteByBlogIdAndArtistId(testBlog.getId(), newArtist.getId());
        entityManager.flush();

        // 検証
        List<BlogArtist> result = blogArtistRepository.findByBlogId(testBlog.getId());
        assertThat(result).hasSize(2);
    }

    @Test @DisplayName("deleteByBlogId_正常系_ブログに関連するアーティストが存在する場合")
    void deleteByBlogId_正常系_ブログに関連するアーティストが存在する場合() {
        // 実行
        blogArtistRepository.deleteByBlogId(testBlog.getId());
        entityManager.flush();

        // 検証
        List<BlogArtist> result = blogArtistRepository.findByBlogId(testBlog.getId());
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("deleteByBlogId_正常系_ブログに関連するアーティストが存在しない場合")
    void deleteByBlogId_正常系_ブログに関連するアーティストが存在しない場合() {
        // 準備
        Blog newBlog = testDataSetup.createTestBlog(testUser);

        // 実行
        blogArtistRepository.deleteByBlogId(newBlog.getId());
        entityManager.flush();

        // 検証
        List<BlogArtist> result = blogArtistRepository.findByBlogId(newBlog.getId());
        assertThat(result).isEmpty();
    }
}