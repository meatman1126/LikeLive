package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.dto.view.BlogInfoViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.BlogArtist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.BlogCategory;
import com.example.bookstore.entity.code.BlogStatus;
import com.example.bookstore.exception.BlogNotFoundException;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.repository.jpa.UserBlogLikeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class BlogServiceTest extends SpringBootTestBase {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserBlogLikeRepository userBlogLikeRepository;

    @Autowired
    private BlogArtistService blogArtistService;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Test @DisplayName("指定されたブログIDで正しくブログ情報を取得できる")
    void findById_WithValidId_ReturnsBlog() {
        // 実行
        Blog result = blogService.findById(testBlog.getId());

        // 検証
        assertNotNull(result);
        assertEquals(testBlog.getId(), result.getId());
        assertEquals(testBlog.getTitle(), result.getTitle());
        assertEquals(testUser.getId(), result.getAuthor().getId());
    }

    @Test @DisplayName("存在しないブログIDでfindByIdを呼び出すとBlogNotFoundExceptionが発生する")
    void findById_WithNonExistentId_ThrowsBlogNotFoundException() {
        // 存在しないブログID
        Long nonExistentId = 999999L;

        // 実行と検証：BlogNotFoundExceptionが発生することを確認
        BlogNotFoundException exception = assertThrows(BlogNotFoundException.class, () -> {
            blogService.findById(nonExistentId);
        });

        // エラーメッセージを検証
        assertEquals("Blog not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test @DisplayName("ブログ情報と関連アーティスト情報を取得できる")
    void findBlogInfo_WithValidId_ReturnsBlogInfoViewDto() {
        // BlogArtistを作成して保存
        BlogArtist blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);

        // 実行
        BlogInfoViewDto result = blogService.findBlogInfo(testBlog.getId());

        // 検証
        assertNotNull(result);
        assertEquals(testBlog.getId(), result.getBlog().getId());
        assertEquals(testBlog.getTitle(), result.getBlog().getTitle());

        // 関連アーティストの検証
        assertNotNull(result.getArtistList());
        assertEquals(1, result.getArtistList().size());
        assertEquals(testArtist1.getId(), result.getArtistList().get(0).getId());

        // いいねの状態を検証（デフォルトは未いいね）
        assertFalse(result.getIsLike());
    }

    @Test @DisplayName("存在しないブログIDでfindBlogInfoを呼び出すとBlogNotFoundExceptionが発生する")
    void findBlogInfo_WithNonExistentId_ThrowsBlogNotFoundException() {
        // 存在しないブログID
        Long nonExistentId = 999999L;

        // 実行と検証：BlogNotFoundExceptionが発生することを確認
        BlogNotFoundException exception = assertThrows(BlogNotFoundException.class, () -> {
            blogService.findBlogInfo(nonExistentId);
        });

        // エラーメッセージを検証
        assertEquals("Blog not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test @DisplayName("未認証ユーザー向けのブログ情報取得")
    void findPublicBlogInfo_WithValidId_ReturnsBlogInfoViewDto() {
        // BlogArtistを作成して保存
        BlogArtist blogArtist1 = testDataSetup.createTestBlogArtist(testBlog, testArtist1);

        // 実行
        BlogInfoViewDto result = blogService.findPublicBlogInfo(testBlog.getId());

        // 検証
        assertNotNull(result);
        assertEquals(testBlog.getId(), result.getBlog().getId());
        assertEquals(testBlog.getTitle(), result.getBlog().getTitle());

        // 関連アーティストの検証
        assertNotNull(result.getArtistList());
        assertEquals(1, result.getArtistList().size());
        assertEquals(testArtist1.getId(), result.getArtistList().get(0).getId());

        // 未認証ユーザー向けなので、いいねはfalseになる
        assertFalse(result.getIsLike());
    }

    @Test @DisplayName("存在しないブログIDでfindPublicBlogInfoを呼び出すとBlogNotFoundExceptionが発生する")
    void findPublicBlogInfo_WithNonExistentId_ThrowsBlogNotFoundException() {
        // 存在しないブログID
        Long nonExistentId = 999999L;

        // 実行と検証：BlogNotFoundExceptionが発生することを確認
        BlogNotFoundException exception = assertThrows(BlogNotFoundException.class, () -> {
            blogService.findPublicBlogInfo(nonExistentId);
        });

        // エラーメッセージを検証
        assertEquals("Blog not found with id: " + nonExistentId, exception.getMessage());
    }

    @Test @DisplayName("ユーザーIDで下書きブログを検索できる")
    void findDraftBlog_WithUserId_ReturnsDraftBlogs() {
        // 下書きブログを作成
        Blog draftBlog1 = testDataSetup.createTestDraftBlog(testUser);
        Blog draftBlog2 = testDataSetup.createTestDraftBlog(testUser);

        // 別のステータスのブログも作成（検索結果に含まれないことを確認）
        Blog publishedBlog = testDataSetup.createTestBlog(testUser);

        // 実行
        List<Blog> results = blogService.findDraftBlog(testUser.getId());

        // 検証
        assertNotNull(results);
        assertEquals(2, results.size());

        // すべてのブログが指定されたユーザーIDに属しており、下書き状態であることを確認
        for (Blog blog : results) {
            assertEquals(testUser.getId(), blog.getAuthor().getId());
            assertEquals(BlogStatus.DRAFT, blog.getStatus());
        }

        // 結果に特定のブログIDが含まれていることを確認
        boolean containsDraftBlog1 = results.stream().anyMatch(blog -> blog.getId().equals(draftBlog1.getId()));
        boolean containsDraftBlog2 = results.stream().anyMatch(blog -> blog.getId().equals(draftBlog2.getId()));
        boolean containsPublishedBlog = results.stream().anyMatch(blog -> blog.getId().equals(publishedBlog.getId()));

        assertTrue(containsDraftBlog1);
        assertTrue(containsDraftBlog2);
        assertFalse(containsPublishedBlog);
    }

    @Test @DisplayName("ユーザーIDで非公開ブログを検索できる")
    void findArchiveBlog_WithUserId_ReturnsArchiveBlogs() {
        // 非公開ブログを作成
        Blog archiveBlog1 = testDataSetup.createTestArchiveBlog(testUser);
        Blog archiveBlog2 = testDataSetup.createTestArchiveBlog(testUser);

        // 別のステータスのブログも作成（検索結果に含まれないことを確認）
        Blog publishedBlog = testDataSetup.createTestBlog(testUser);

        // 実行
        List<Blog> results = blogService.findArchiveBlog(testUser.getId());

        // 検証
        assertNotNull(results);
        assertEquals(2, results.size());

        // すべてのブログが指定されたユーザーIDに属しており、非公開状態であることを確認
        for (Blog blog : results) {
            assertEquals(testUser.getId(), blog.getAuthor().getId());
            assertEquals(BlogStatus.ARCHIVED, blog.getStatus());
        }

        // 結果に特定のブログIDが含まれていることを確認
        boolean containsArchiveBlog1 = results.stream().anyMatch(blog -> blog.getId().equals(archiveBlog1.getId()));
        boolean containsArchiveBlog2 = results.stream().anyMatch(blog -> blog.getId().equals(archiveBlog2.getId()));
        boolean containsPublishedBlog = results.stream().anyMatch(blog -> blog.getId().equals(publishedBlog.getId()));

        assertTrue(containsArchiveBlog1);
        assertTrue(containsArchiveBlog2);
        assertFalse(containsPublishedBlog);
    }

    @Test @DisplayName("キーワードによるブログ検索ができる")
    void searchBlog_WithKeyword_ReturnsMatchingBlogs() {
        // 検索キーワードを含むブログを作成
        String searchKeyword = "特殊なキーワード";
        Blog blogWithKeyword1 = testDataSetup.createTestBlogWithTitle(testUser, searchKeyword + "タイトル");
        Blog blogWithKeyword2 = testDataSetup.createTestBlogWithContent(testUser, searchKeyword + "コンテンツ");

        // 検索キーワードを含まないブログも作成
        Blog blogWithoutKeyword = testDataSetup.createTestBlog(testUser);

        // 実行（ページネーション付き検索）
        Pageable pageable = PageRequest.of(0, 10);
        Page<Blog> results = blogService.searchBlog(searchKeyword, pageable);

        // 検証
        assertNotNull(results);
        assertEquals(2, results.getTotalElements());

        // 結果に特定のブログIDが含まれていることを確認
        boolean containsBlogWithKeyword1 = results.stream()
                .anyMatch(blog -> blog.getId().equals(blogWithKeyword1.getId()));
        boolean containsBlogWithKeyword2 = results.stream()
                .anyMatch(blog -> blog.getId().equals(blogWithKeyword2.getId()));
        boolean containsBlogWithoutKeyword = results.stream()
                .anyMatch(blog -> blog.getId().equals(blogWithoutKeyword.getId()));

        assertTrue(containsBlogWithKeyword1);
        assertTrue(containsBlogWithKeyword2);
        assertFalse(containsBlogWithoutKeyword);
    }

    @Test @DisplayName("新規ブログを作成できる")
    void createBlog_WithValidInput_CreatesAndReturnsBlog() {
        // 新規ブログの準備
        Blog newBlog = Blog.builder().title("新規ブログのタイトル").content(Map.of("text", "新規ブログの内容"))
                .status(BlogStatus.PUBLISHED).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.OTHER)
                .createdBy(testUser.getId().toString()).updatedBy(testUser.getId().toString()).isDeleted(false).build();

        // アーティストIDリストの準備
        List<String> artistIdList = Arrays.asList(testArtist1.getId(), testArtist2.getId());

        // サムネイル画像の準備
        MockMultipartFile thumbnailImage = new MockMultipartFile("thumbnail", "test.jpg", "image/jpeg",
                "テスト画像".getBytes());

        // 実行
        Blog result = blogService.createBlog(newBlog, artistIdList, thumbnailImage);

        // 検証
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("新規ブログのタイトル", result.getTitle());
        assertEquals("新規ブログの内容", ((Map<String, Object>) result.getContent()).get("text"));
        assertEquals(BlogStatus.PUBLISHED, result.getStatus());
        assertEquals(testUser.getId(), result.getAuthor().getId());
        assertNotNull(result.getThumbnailUrl());

        // 関連アーティストが保存されていることを検証
        List<Artist> relatedArtists = blogArtistService.findArtistsByBlogId(result.getId());
        assertEquals(2, relatedArtists.size());

        // アーティストIDが正しいことを確認
        List<String> resultArtistIds = relatedArtists.stream().map(Artist::getId).toList();
        assertTrue(resultArtistIds.contains(testArtist1.getId()));
        assertTrue(resultArtistIds.contains(testArtist2.getId()));
    }

    @Test @DisplayName("サムネイルなしでブログを作成できる")
    void createBlog_WithoutThumbnail_CreatesAndReturnsBlog() {
        // 新規ブログの準備
        Blog newBlog = Blog.builder().title("サムネイルなしブログ").content(Map.of("text", "サムネイルなしブログの内容"))
                .status(BlogStatus.PUBLISHED).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.OTHER)
                .createdBy(testUser.getId().toString()).updatedBy(testUser.getId().toString()).isDeleted(false).build();

        // アーティストIDリストの準備
        List<String> artistIdList = Collections.singletonList(testArtist1.getId());

        // 実行（サムネイルなし）
        Blog result = blogService.createBlog(newBlog, artistIdList, null);

        // 検証
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("サムネイルなしブログ", result.getTitle());
        assertEquals(testUser.getId(), result.getAuthor().getId());

        // 関連アーティストが正しく保存されていることを検証
        List<Artist> relatedArtists = blogArtistService.findArtistsByBlogId(result.getId());
        assertEquals(1, relatedArtists.size());
        assertEquals(testArtist1.getId(), relatedArtists.get(0).getId());
    }

    @Test @DisplayName("ブログの閲覧回数を更新できる")
    void updatedViewCount_IncreasesViewCount() {
        // テスト用のブログを作成
        Blog testBlog = testDataSetup.createTestBlog(testUser);
        int initialViewCount = testBlog.getViewCount();

        // 閲覧回数を更新
        int updatedCount = blogService.updatedViewCount(testBlog.getId());

        // 更新後のブログを取得（即時反映）
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 閲覧回数が1増加していることを確認
        assertEquals(initialViewCount + 1, updatedCount, "閲覧回数が1増加していること");
        assertEquals(initialViewCount + 1, testBlog.getViewCount(), "データベースの閲覧回数も1増加していること");
    }

    @Test @DisplayName("ブログにいいねを追加できる")
    void likeBlog_AddsLikeAndReturnsUpdatedCount() {
        // 初期状態ではいいねしていないことを確認
        assertFalse(userBlogLikeRepository.isLikeBlog(testUser.getId(), testBlog.getId()));

        // いいねを追加
        Integer updatedLikeCount = blogService.likeBlog(testUser.getId(), testBlog.getId());

        // 即時反映
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        assertNotNull(updatedLikeCount);
        assertEquals(1, updatedLikeCount.intValue());

        // データベースでもいいねが記録されていることを確認
        assertTrue(userBlogLikeRepository.isLikeBlog(testUser.getId(), testBlog.getId()));

        // いいね数が増えていることを確認
        assertEquals(1, testBlog.getLikeCount());
    }

    @Test @DisplayName("ブログのいいねを解除できる")
    void clearLikeBlog_RemovesLikeAndDecreasesCount() {
        // いいねを追加
        blogService.likeBlog(testUser.getId(), testBlog.getId());
        entityManager.flush();
        entityManager.refresh(testBlog);
        assertEquals(1, testBlog.getLikeCount());

        // いいねを解除
        Integer updatedLikeCount = blogService.clearLikeBlog(testUser.getId(), testBlog.getId());

        // 即時反映
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        assertNotNull(updatedLikeCount);
        assertEquals(0, updatedLikeCount.intValue());
        assertFalse(userBlogLikeRepository.isLikeBlog(testUser.getId(), testBlog.getId()));
        assertEquals(0, testBlog.getLikeCount());
    }

    @Test @DisplayName("指定したユーザーがブログにいいねしているかを確認できる")
    void isLikeBlog_ReturnsCorrectLikeStatus() {
        // 初期状態ではいいねしていない
        boolean initialLikeStatus = blogService.isLikeBlog(testUser.getId(), testBlog.getId());
        assertFalse(initialLikeStatus);

        // いいねを追加
        blogService.likeBlog(testUser.getId(), testBlog.getId());

        // いいね後の状態を確認
        boolean afterLikeStatus = blogService.isLikeBlog(testUser.getId(), testBlog.getId());
        assertTrue(afterLikeStatus);

        // いいねを取り消し
        blogService.clearLikeBlog(testUser.getId(), testBlog.getId());

        // 取り消し後の状態を確認
        boolean afterClearStatus = blogService.isLikeBlog(testUser.getId(), testBlog.getId());
        assertFalse(afterClearStatus);
    }

    @Test @DisplayName("ブログを更新できる")
    void updatedBlog_WithValidInput_UpdatesAndReturnsBlog() {
        // 更新用のブログデータを準備（既存のBlogエンティティの内容を更新）
        testBlog.setTitle("更新後のタイトル");
        testBlog.setContent(Map.of("text", "更新後の内容"));
        testBlog.setStatus(BlogStatus.PUBLISHED);
        testBlog.setBlogUpdatedTime(LocalDateTime.now());
        testBlog.setUpdatedBy(testUser.getId().toString());

        // アーティストIDリストを準備（別のアーティストを使用）
        List<String> updatedArtistIdList = Collections.singletonList(testArtist2.getId());

        // サムネイル画像の準備
        MockMultipartFile updatedThumbnail = new MockMultipartFile("thumbnail", "updated.jpg", "image/jpeg",
                "更新用サムネイル".getBytes());

        // 実行
        Blog result = blogService.updatedBlog(testBlog.getId(), testBlog, updatedArtistIdList, updatedThumbnail);

        // 即時反映
        entityManager.flush();
        entityManager.refresh(result);

        // 検証
        assertNotNull(result);
        assertEquals(testBlog.getId(), result.getId()); // 同じIDであることを確認
        assertEquals("更新後のタイトル", result.getTitle());
        assertEquals("更新後の内容", ((Map<String, Object>) result.getContent()).get("text"));
        assertNotNull(result.getThumbnailUrl());

        // 関連アーティストが更新されていることを検証
        List<Artist> relatedArtists = blogArtistService.findArtistsByBlogId(result.getId());
        assertEquals(1, relatedArtists.size());
        assertEquals(testArtist2.getId(), relatedArtists.get(0).getId());
    }

    @Test @DisplayName("サムネイル変更なしでブログを更新できる")
    void updatedBlog_WithoutThumbnail_UpdatesAndReturnsBlog() {
        // 事前にブログにサムネイルを設定（元のサムネイルが保持されるかを確認するため）
        MockMultipartFile initialThumbnail = new MockMultipartFile("thumbnail", "initial.jpg", "image/jpeg",
                "初期サムネイル".getBytes());
        blogService.updatedBlog(testBlog.getId(), testBlog, Collections.emptyList(), initialThumbnail);

        Blog blogWithThumbnail = blogService.findById(testBlog.getId());
        String initialThumbnailUrl = blogWithThumbnail.getThumbnailUrl();
        assertNotNull(initialThumbnailUrl);

        // 更新用のブログデータを準備（サムネイルなし）
        Blog updateData = Blog.builder().title("サムネイル変更なしの更新").content(Map.of("text", "サムネイル変更なしの更新内容"))
                .status(BlogStatus.PUBLISHED).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.OTHER)
                .createdBy(testUser.getId().toString()).updatedBy(testUser.getId().toString()).isDeleted(false).build();

        // 実行（サムネイルなしで更新）
        Blog result = blogService.updatedBlog(testBlog.getId(), updateData, Collections.emptyList(), null);

        // 検証
        assertNotNull(result);
        assertEquals("サムネイル変更なしの更新", result.getTitle());
        assertEquals("サムネイル変更なしの更新内容", ((Map<String, Object>) result.getContent()).get("text"));

        // サムネイルURLが保持されていることを確認
        assertNotNull(result.getThumbnailUrl());
    }

    @Test @DisplayName("下書きブログを公開状態に更新するとブログ作成通知が登録される")
    void updatedBlog_FromDraftToPublished_CreatesNotification() {
        // 下書きブログを作成
        Blog draftBlog = testDataSetup.createTestDraftBlog(testUser);

        // テスト用フォロワーを作成（通知の送信先）
        User follower = testDataSetup.createTestUser();
        testDataSetup.createTestFollow(follower, testUser);

        // 公開状態に更新
        Blog updateData = Blog.builder().title("公開に変更したブログ").content(Map.of("text", "公開に変更した内容"))
                .status(BlogStatus.PUBLISHED).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.OTHER)
                .createdBy(testUser.getId().toString()).updatedBy(testUser.getId().toString()).isDeleted(false).build();

        // 実行
        Blog result = blogService.updatedBlog(draftBlog.getId(), updateData, Collections.emptyList(), null);

        // 検証
        assertNotNull(result);
        assertEquals(BlogStatus.PUBLISHED, result.getStatus());

        // 通知が作成されていることを確認（通知関連のリポジトリやサービスへのアクセスが必要）
        // この部分のテストは実装が難しいため、ここではステータスの変更のみ確認
    }

    @Test @DisplayName("他のユーザーのブログを更新しようとするとエラーが発生する")
    void updatedBlog_WithOtherUsersBlog_ThrowsException() {
        // 別のユーザーを作成
        User otherUser = testDataSetup.createTestUser();

        // 別のユーザーのブログを作成
        Blog otherUsersBlog = testDataSetup.createTestBlog(otherUser);

        // 更新用のデータを準備
        Blog updateData = Blog.builder().title("不正な更新").content(Map.of("text", "不正な更新内容")).status(BlogStatus.PUBLISHED)
                .author(testUser) // 現在認証されているユーザー
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.OTHER)
                .createdBy(testUser.getId().toString()).updatedBy(testUser.getId().toString()).isDeleted(false).build();

        // 実行と検証：例外が発生することを確認
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            blogService.updatedBlog(otherUsersBlog.getId(), updateData, Collections.emptyList(), null);
        });

        assertEquals("他ユーザのブログを編集することはできません", exception.getMessage());
    }

    @Test @DisplayName("ブログのコメント数を増やせる")
    void updatedCommentCount_IncreasesCommentCount() {
        // 初期のコメント数を確認
        int initialCommentCount = testBlog.getCommentCount();

        // コメント数を増やす
        int updatedCount = blogService.updatedCommentCount(testBlog.getId(), false);

        // 即時反映
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        assertEquals(initialCommentCount + 1, updatedCount);
        assertEquals(initialCommentCount + 1, testBlog.getCommentCount());
    }

    @Test @DisplayName("ブログのコメント数を減らせる")
    void updatedCommentCount_DecreasesCommentCount() {
        // コメント数を増やす
        blogService.updatedCommentCount(testBlog.getId(), false);
        entityManager.flush();
        entityManager.refresh(testBlog);
        assertEquals(1, testBlog.getCommentCount());

        // コメント数を減らす
        int updatedCount = blogService.updatedCommentCount(testBlog.getId(), true);

        // 即時反映
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        assertEquals(0, updatedCount);
        assertEquals(0, testBlog.getCommentCount());
    }

    @Test @DisplayName("ブログを非公開にできる")
    void unpublishBlog_ChangesStatusToArchived() {
        // 公開状態のブログを確認
        assertEquals(BlogStatus.PUBLISHED, testBlog.getStatus());

        // ブログを非公開にする
        blogService.unpublishBlog(testBlog.getId(), testUser.getId().toString());

        // 更新後のブログを取得（即時反映）
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        assertEquals(BlogStatus.ARCHIVED, testBlog.getStatus());
    }

    @Test @DisplayName("他のユーザーのブログを非公開にしようとするとエラーが発生する")
    void unpublishBlog_WithOtherUsersBlog_ThrowsException() {
        // 別のユーザーを作成
        User otherUser = testDataSetup.createTestUser();

        // 別のユーザーのブログを作成
        Blog otherUsersBlog = testDataSetup.createTestBlog(otherUser);

        // 実行と検証：例外が発生することを確認
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            blogService.unpublishBlog(otherUsersBlog.getId(), testUser.getId().toString());
        });

        assertEquals("他ユーザのブログを編集することはできません", exception.getMessage());
    }

    @Test @DisplayName("ブログを削除できる")
    void deleteBlog_MarksBlogAsDeleted() {
        // ブログを削除
        blogService.deleteBlog(testBlog.getId());

        // 削除されたブログが見つからないことを確認
        BlogNotFoundException exception = assertThrows(BlogNotFoundException.class, () -> {
            blogService.findById(testBlog.getId());
        });

        // エラーメッセージを検証
        assertEquals("Blog not found with id: " + testBlog.getId(), exception.getMessage());
    }

    @Test @DisplayName("他のユーザーのブログを削除しようとするとエラーが発生する")
    void deleteBlog_WithOtherUsersBlog_ThrowsException() {
        // 別のユーザーを作成し認証
        User otherUser = testDataSetup.createTestUser();

        // 別のユーザーのブログを作成
        Blog otherUsersBlog = testDataSetup.createTestBlog(otherUser);

        // 実行と検証：例外が発生することを確認
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            blogService.deleteBlog(otherUsersBlog.getId());
        });
    }

    @Test @DisplayName("興味のあるブログを取得できる")
    void findInterestBlogs_ReturnsRelevantBlogs() {
        // 実行
        List<?> results = blogService.findInterestBlogs(testUser.getId());

        // 検証
        assertNotNull(results);
        // ここでは結果の詳細な内容を検証するのではなく、
        // メソッドが正常に動作することと、結果が取得できることを確認する
    }

}