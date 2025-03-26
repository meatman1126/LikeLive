package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.repository.DashboardBlogRepositoryDto;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.BlogCategory;
import com.example.bookstore.entity.code.BlogStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class BlogRepositoryTest extends JpaTestBase {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Blog testBlog;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser = testDataSetup.createTestUser();
        testBlog = testDataSetup.createTestBlog(testUser);
    }

    @Test @DisplayName("findById_正常系_存在するブログの場合")
    void findById_正常系_存在するブログの場合() {
        // 実行
        Optional<Blog> result = blogRepository.findById(testBlog.getId());

        // 検証
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testBlog.getId());
        assertThat(result.get().getTitle()).isEqualTo(testBlog.getTitle());
        assertThat(result.get().getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test @DisplayName("findById_正常系_存在しないブログの場合")
    void findById_正常系_存在しないブログの場合() {
        // 実行
        Optional<Blog> result = blogRepository.findById(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findById_正常系_論理削除されたブログの場合")
    void findById_正常系_論理削除されたブログの場合() {
        // テストブログを論理削除
        testBlog.setIsDeleted(true);
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 実行
        Optional<Blog> result = blogRepository.findById(testBlog.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findPublishedBlogsByUserId_正常系_公開中のブログが存在する場合")
    void findPublishedBlogsByUserId_正常系_公開中のブログが存在する場合() {
        // 実行
        List<Blog> result = blogRepository.findPublishedBlogsByUserId(testUser.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBlog.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(BlogStatus.PUBLISHED);
    }

    @Test @DisplayName("findPublishedBlogsByUserId_正常系_公開中のブログが存在しない場合")
    void findPublishedBlogsByUserId_正常系_公開中のブログが存在しない場合() {
        // 準備
        testBlog.setStatus(BlogStatus.DRAFT);
        blogRepository.save(testBlog);
        entityManager.flush();

        // 実行
        List<Blog> result = blogRepository.findPublishedBlogsByUserId(testUser.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findDraftBlogsByUserId_正常系_下書きのブログが存在する場合")
    void findDraftBlogsByUserId_正常系_下書きのブログが存在する場合() {
        // 準備
        testBlog.setStatus(BlogStatus.DRAFT);
        blogRepository.save(testBlog);
        entityManager.flush();

        // 実行
        List<Blog> result = blogRepository.findDraftBlogsByUserId(testUser.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBlog.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(BlogStatus.DRAFT);
    }

    @Test @DisplayName("findArchiveBlogsByUserId_正常系_非公開のブログが存在する場合")
    void findArchiveBlogsByUserId_正常系_非公開のブログが存在する場合() {
        // 準備
        testBlog.setStatus(BlogStatus.ARCHIVED);
        blogRepository.save(testBlog);
        entityManager.flush();

        // 実行
        List<Blog> result = blogRepository.findArchiveBlogsByUserId(testUser.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBlog.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(BlogStatus.ARCHIVED);
    }

    @Test @DisplayName("searchBlogsByKeyword_正常系_タイトルで検索")
    void searchBlogsByKeyword_正常系_タイトルで検索() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        Page<Blog> result = blogRepository.searchBlogsByKeyword("Test", pageable);

        // 検証
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testBlog.getId());
        assertThat(result.getContent().get(0).getTitle()).contains("Test");
    }

    @Test @DisplayName("searchBlogsByKeyword_正常系_コンテンツで検索")
    void searchBlogsByKeyword_正常系_コンテンツで検索() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        Page<Blog> result = blogRepository.searchBlogsByKeyword("Content", pageable);

        // 検証
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testBlog.getId());
    }

    @Test @DisplayName("searchBlogsByKeyword_正常系_大文字小文字を区別しない検索")
    void searchBlogsByKeyword_正常系_大文字小文字を区別しない検索() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        Page<Blog> result = blogRepository.searchBlogsByKeyword("test", pageable);

        // 検証
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(testBlog.getId());
    }

    @Test @DisplayName("searchBlogsByKeyword_正常系_検索結果なし")
    void searchBlogsByKeyword_正常系_検索結果なし() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        Page<Blog> result = blogRepository.searchBlogsByKeyword("NonExistent", pageable);

        // 検証
        assertThat(result.getContent()).isEmpty();
    }

    @Test @DisplayName("searchBlogsByKeyword_正常系_キーワードが空文字の場合")
    void searchBlogsByKeyword_正常系_キーワードが空文字の場合() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        Page<Blog> result = blogRepository.searchBlogsByKeyword("", pageable);

        // 検証
        assertThat(result.getContent()).isEmpty();
    }

    @Test @DisplayName("update_正常系_全項目更新")
    void update_正常系_全項目更新() {
        // 準備
        Blog updateBlog = Blog.builder().title("Updated Title").content(Map.of("text", "Updated content"))
                .status(BlogStatus.DRAFT).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.REPORT).isDeleted(false)
                .updatedBy("testUser").build();

        // 実行
        blogRepository.update(testBlog.getId(), updateBlog);
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        Optional<Blog> result = blogRepository.findById(testBlog.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getStatus()).isEqualTo(BlogStatus.DRAFT);
        assertThat(result.get().getCategory()).isEqualTo(BlogCategory.REPORT);
    }

    @Test @DisplayName("update_異常系_存在しないブログID")
    void update_異常系_存在しないブログID() {
        // 準備
        Blog updateBlog = Blog.builder().title("Updated Title").content(Map.of("text", "Updated content"))
                .status(BlogStatus.DRAFT).author(testUser).blogCreatedTime(LocalDateTime.now())
                .blogUpdatedTime(LocalDateTime.now()).category(BlogCategory.REPORT).isDeleted(false)
                .updatedBy("testUser").build();

        // 実行
        blogRepository.update(999L, updateBlog);
        entityManager.flush();
        entityManager.refresh(testBlog);
        // 検証
        Optional<Blog> result = blogRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("unpublishBlog_正常系")
    void unpublishBlog_正常系() {
        // 実行
        int result = blogRepository.unpublishBlog(testBlog.getId(), testUser.getId().toString());
        entityManager.flush();
        entityManager.refresh(testBlog);
        // 検証
        assertThat(result).isEqualTo(1);
        Optional<Blog> updatedBlog = blogRepository.findById(testBlog.getId());
        assertThat(updatedBlog).isPresent();
        assertThat(updatedBlog.get().getStatus()).isEqualTo(BlogStatus.ARCHIVED);
    }

    @Test @DisplayName("unpublishBlog_異常系_存在しないブログID")
    void unpublishBlog_異常系_存在しないブログID() {
        // 実行
        int result = blogRepository.unpublishBlog(999L, testUser.getId().toString());

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test @DisplayName("delete_正常系")
    void delete_正常系() {
        // 実行
        blogRepository.delete(testBlog.getId(), testUser.getId().toString());
        entityManager.flush();
        entityManager.refresh(testBlog);
        // 検証
        Optional<Blog> result = blogRepository.findById(testBlog.getId());
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("delete_異常系_存在しないブログID")
    void delete_異常系_存在しないブログID() {
        // 実行
        blogRepository.delete(999L, testUser.getId().toString());
        entityManager.flush();

        // 検証
        Optional<Blog> result = blogRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("updateViewCount_正常系")
    void updateViewCount_正常系() {
        // 準備
        int newViewCount = 100;

        // 実行
        blogRepository.updateViewCount(testBlog.getId(), newViewCount, testUser.getId().toString());
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        Optional<Blog> result = blogRepository.findById(testBlog.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getViewCount()).isEqualTo(newViewCount);
    }

    @Test @DisplayName("updateViewCount_異常系_存在しないブログID")
    void updateViewCount_異常系_存在しないブログID() {
        // 準備
        int newViewCount = 100;

        // 実行
        blogRepository.updateViewCount(999L, newViewCount, testUser.getId().toString());
        entityManager.flush();

        // 検証
        Optional<Blog> result = blogRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("updateLikeCount_正常系")
    void updateLikeCount_正常系() {
        // 準備
        int newLikeCount = 50;

        // 実行
        blogRepository.updateLikeCount(testBlog.getId(), newLikeCount, testUser.getId().toString());
        entityManager.flush();
        entityManager.refresh(testBlog);

        // 検証
        Optional<Blog> result = blogRepository.findById(testBlog.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getLikeCount()).isEqualTo(newLikeCount);
    }

    @Test @DisplayName("updateLikeCount_異常系_存在しないブログID")
    void updateLikeCount_異常系_存在しないブログID() {
        // 準備
        int newLikeCount = 50;

        // 実行
        blogRepository.updateLikeCount(999L, newLikeCount, testUser.getId().toString());
        entityManager.flush();

        // 検証
        Optional<Blog> result = blogRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("updateCommentCount_正常系")
    void updateCommentCount_正常系() {
        // 準備
        int newCommentCount = 30;

        // 実行
        blogRepository.updateCommentCount(testBlog.getId(), newCommentCount, testUser.getId().toString());
        entityManager.flush();
        entityManager.refresh(testBlog);
        // 検証
        Optional<Blog> result = blogRepository.findById(testBlog.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getCommentCount()).isEqualTo(newCommentCount);
    }

    @Test @DisplayName("updateCommentCount_異常系_存在しないブログID")
    void updateCommentCount_異常系_存在しないブログID() {
        // 準備
        int newCommentCount = 30;

        // 実行
        blogRepository.updateCommentCount(999L, newCommentCount, testUser.getId().toString());
        entityManager.flush();

        // 検証
        Optional<Blog> result = blogRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("findInterestBlogs_正常系")
    void findInterestBlogs_正常系() {
        // 準備
        User otherUser = testDataSetup.createTestUser();
        Blog otherBlog = testDataSetup.createTestBlog(otherUser);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        List<DashboardBlogRepositoryDto> result = blogRepository.findInterestBlogs(testUser.getId(),
                BlogStatus.PUBLISHED, pageable);

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(otherBlog.getId());
        assertThat(result.get(0).getBlogTitle()).isEqualTo(otherBlog.getTitle());
        assertThat(result.get(0).getAuthorName()).isEqualTo(otherUser.getDisplayName());
    }

    @Test @DisplayName("findInterestBlogs_正常系_非公開ブログは除外")
    void findInterestBlogs_正常系_非公開ブログは除外() {
        // 準備
        testBlog.setStatus(BlogStatus.ARCHIVED);
        blogRepository.save(testBlog);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "blogCreatedTime"));

        // 実行
        List<DashboardBlogRepositoryDto> result = blogRepository.findInterestBlogs(testUser.getId(),
                BlogStatus.PUBLISHED, pageable);

        // 検証
        assertThat(result).isEmpty();
    }
}