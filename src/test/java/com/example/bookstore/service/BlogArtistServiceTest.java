package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.BlogArtist;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.jpa.BlogArtistRepository;

public class BlogArtistServiceTest extends SpringBootTestBase {

    @Autowired
    private BlogArtistService blogArtistService;

    @Autowired
    private BlogArtistRepository blogArtistRepository;

    private User testUser;
    private Blog testBlog;
    private Artist testArtist1;
    private Artist testArtist2;

    @BeforeEach
    void setUp() {
        // テストユーザーを作成し、認証情報を設定
        testUser = createUserAndSetupAuthentication(testDataSetup);

        // テストブログを作成
        testBlog = testDataSetup.createTestBlog(testUser);

        // テストアーティストを作成
        testArtist1 = testDataSetup.createTestArtist();
        testArtist2 = testDataSetup.createTestArtist();
    }

    @Test @DisplayName("アーティストリストをブログに関連付けて保存できる")
    void saveBlogArtist_WithArtistList_SavesBlogArtists() {
        // アーティストIDリストの準備
        List<String> artistIdList = Arrays.asList(testArtist1.getId(), testArtist2.getId());

        // 実行
        List<BlogArtist> savedBlogArtists = blogArtistService.saveBlogArtist(testBlog, artistIdList);

        // 検証
        assertNotNull(savedBlogArtists);
        assertEquals(2, savedBlogArtists.size());

        // 各BlogArtistの内容を検証
        for (BlogArtist blogArtist : savedBlogArtists) {
            assertNotNull(blogArtist.getId());
            assertEquals(testBlog.getId(), blogArtist.getBlog().getId());
            assertTrue(artistIdList.contains(blogArtist.getArtist().getId()));
            assertEquals(testUser.getId().toString(), blogArtist.getCreatedBy());
            assertEquals(testUser.getId().toString(), blogArtist.getUpdatedBy());
        }

        // データベースから取得して再検証
        List<BlogArtist> dbBlogArtists = blogArtistRepository.findByBlogId(testBlog.getId());
        assertEquals(2, dbBlogArtists.size());
    }

    @Test @DisplayName("空のアーティストリストを渡すと空のリストが返される")
    void saveBlogArtist_WithEmptyArtistList_ReturnsEmptyList() {
        // 空のアーティストリスト
        List<String> emptyArtistIdList = Collections.emptyList();

        // 実行
        List<BlogArtist> savedBlogArtists = blogArtistService.saveBlogArtist(testBlog, emptyArtistIdList);

        // 検証
        assertNotNull(savedBlogArtists);
        assertTrue(savedBlogArtists.isEmpty());

        // DBにも保存されていないことを確認
        List<BlogArtist> dbBlogArtists = blogArtistRepository.findByBlogId(testBlog.getId());
        assertTrue(dbBlogArtists.isEmpty());
    }

    @Test @DisplayName("nullのアーティストリストを渡すと空のリストが返される")
    void saveBlogArtist_WithNullArtistList_ReturnsEmptyList() {
        // nullのアーティストリスト
        List<String> nullArtistIdList = null;

        // 実行
        List<BlogArtist> savedBlogArtists = blogArtistService.saveBlogArtist(testBlog, nullArtistIdList);

        // 検証
        assertNotNull(savedBlogArtists);
        assertTrue(savedBlogArtists.isEmpty());

        // DBにも保存されていないことを確認
        List<BlogArtist> dbBlogArtists = blogArtistRepository.findByBlogId(testBlog.getId());
        assertTrue(dbBlogArtists.isEmpty());
    }

    @Test @DisplayName("既存のBlogArtistがある場合は削除されて新しいものが保存される")
    void saveBlogArtist_WithExistingBlogArtists_DeletesOldAndSavesNew() {
        // まず既存のBlogArtistを作成
        BlogArtist existingBlogArtist = testDataSetup.createTestBlogArtist(testBlog, testArtist1);

        // 既存のBlogArtistが保存されていることを確認
        List<BlogArtist> initialBlogArtists = blogArtistRepository.findByBlogId(testBlog.getId());
        assertEquals(1, initialBlogArtists.size());
        assertEquals(testArtist1.getId(), initialBlogArtists.get(0).getArtist().getId());

        // 新しいアーティストIDリストの準備（別のアーティストを使用）
        List<String> newArtistIdList = Collections.singletonList(testArtist2.getId());

        // 実行
        List<BlogArtist> savedBlogArtists = blogArtistService.saveBlogArtist(testBlog, newArtistIdList);

        // 検証
        assertNotNull(savedBlogArtists);
        assertEquals(1, savedBlogArtists.size());
        assertEquals(testArtist2.getId(), savedBlogArtists.get(0).getArtist().getId());

        // データベースから取得して再検証
        List<BlogArtist> dbBlogArtists = blogArtistRepository.findByBlogId(testBlog.getId());
        assertEquals(1, dbBlogArtists.size());
        assertEquals(testArtist2.getId(), dbBlogArtists.get(0).getArtist().getId());
    }

    @Test @DisplayName("ブログIDでBlogArtistを検索できる")
    void getBlogArtistsByBlogId_ReturnsListOfBlogArtists() {
        // BlogArtistを作成して保存
        BlogArtist blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);
        BlogArtist blogArtist2 = testDataSetup.createTestBlogArtist(testBlog, testArtist2);

        // 実行
        List<BlogArtist> blogArtists = blogArtistService.getBlogArtistsByBlogId(testBlog.getId());

        // 検証
        assertNotNull(blogArtists);
        assertEquals(2, blogArtists.size());

        // ブログIDが正しいことを確認
        for (BlogArtist blogArtist : blogArtists) {
            assertEquals(testBlog.getId(), blogArtist.getBlog().getId());
        }
    }

    @Test @DisplayName("ブログIDで関連するアーティストを検索できる")
    void findArtistsByBlogId_ReturnsListOfArtists() {
        // BlogArtistを作成して保存
        BlogArtist blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);
        BlogArtist blogArtist2 = testDataSetup.createTestBlogArtist(testBlog, testArtist2);

        // 実行
        List<Artist> artists = blogArtistService.findArtistsByBlogId(testBlog.getId());

        // 検証
        assertNotNull(artists);
        assertEquals(2, artists.size());

        // 期待するアーティストIDが含まれていることを確認
        List<String> expectedArtistIds = Arrays.asList(testArtist1.getId(), testArtist2.getId());
        for (Artist artist : artists) {
            assertTrue(expectedArtistIds.contains(artist.getId()));
        }
    }

    @Test @DisplayName("アーティストIDでBlogArtistを検索できる")
    void getBlogArtistsByArtistId_ReturnsListOfBlogArtists() {
        // 異なるブログを作成
        Blog testBlog2 = testDataSetup.createTestBlog(testUser);

        // 同一アーティストを使って複数のBlogArtistを作成
        BlogArtist blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);
        BlogArtist blogArtist2 = testDataSetup.createTestBlogArtist(testBlog2, testArtist1);

        // 実行
        List<BlogArtist> blogArtists = blogArtistService.getBlogArtistsByArtistId(testArtist1.getId());

        // 検証
        assertNotNull(blogArtists);
        assertEquals(2, blogArtists.size());

        // アーティストIDが正しいことを確認
        for (BlogArtist blogArtist : blogArtists) {
            assertEquals(testArtist1.getId(), blogArtist.getArtist().getId());
        }
    }
}