package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.repository.ParentCommentRepositoryDto;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class CommentRepositoryTest extends JpaTestBase {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Blog testBlog;
    private Blog testBlog2;
    private Comment parentComment;
    private Comment replyComment;
    private CommentTree commentTree;
    private Comment testComment;
    private Comment deletedComment;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser = testDataSetup.createTestUser();
        testBlog = testDataSetup.createTestBlog(testUser);
        
        // 親コメントの作成
        parentComment = testDataSetup.createTestComment(testUser, testBlog, "Test parent comment");

        // 返信コメントの作成
        replyComment = testDataSetup.createTestComment(testUser, testBlog, "Test reply comment");

        // コメントツリーの作成
        commentTree = testDataSetup.createTestCommentTree(
                parentComment,
                replyComment,
                1,
                testUser.getId().toString()
        );

        testBlog2 = testDataSetup.createTestBlog(testUser);


        testComment = testDataSetup.createTestComment(testUser, testBlog2, "テストコメント");
        deletedComment = testDataSetup.createTestComment(testUser, testBlog2, "削除済みコメント");
        deletedComment.setIsDeleted(true);
    }

    @Test
    @DisplayName("findParentCommentsByBlogId_正常系_親コメントが存在する場合")
    void findParentCommentsByBlogId_正常系_親コメントが存在する場合() {
        // 実行
        List<ParentCommentRepositoryDto> result = commentRepository.findParentCommentsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComment().getId()).isEqualTo(parentComment.getId());
        assertThat(result.get(0).getReplyCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("findParentCommentsByBlogId_正常系_親コメントが存在しない場合")
    void findParentCommentsByBlogId_正常系_親コメントが存在しない場合() {
        // 実行
        List<ParentCommentRepositoryDto> result = commentRepository.findParentCommentsByBlogId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findParentCommentsByBlogId_正常系_論理削除された親コメントは除外")
    void findParentCommentsByBlogId_正常系_論理削除された親コメントは除外() {
        // 準備
        parentComment.setIsDeleted(true);
        commentRepository.save(parentComment);
        entityManager.flush();

        // 実行
        List<ParentCommentRepositoryDto> result = commentRepository.findParentCommentsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("updateCommentContent_正常系")
    void updateCommentContent_正常系() {
        // 準備
        String newContent = "Updated comment content";
        LocalDateTime updateTime = LocalDateTime.now();

        // 実行
        commentRepository.updateCommentContent(
                parentComment.getId(),
                newContent,
                updateTime,
                testUser.getId().toString()
        );
        entityManager.flush();
        entityManager.refresh(parentComment);

        // 検証
        Comment result = commentRepository.findById(parentComment.getId()).orElseThrow();
        assertThat(result.getContent()).isEqualTo(newContent);
        assertThat(result.getCommentUpdatedTime()).isEqualTo(updateTime);
        assertThat(result.getUpdatedBy()).isEqualTo(testUser.getId().toString());
    }

    @Test
    @DisplayName("deleteCommentById_正常系")
    void deleteCommentById_正常系() {
        // 実行
        int result = commentRepository.deleteCommentById(
                parentComment.getId(),
                testUser.getId().toString()
        );
        entityManager.flush();
        entityManager.refresh(parentComment);

        // 検証
        assertThat(result).isEqualTo(1);
        Optional<Comment> deletedCommentOpt = commentRepository.findById(parentComment.getId());
        assertThat(deletedCommentOpt).isEmpty();
    }

    @Test
    @DisplayName("deleteCommentById_異常系_存在しないコメントID")
    void deleteCommentById_異常系_存在しないコメントID() {
        // 実行
        int result = commentRepository.deleteCommentById(
                999L,
                testUser.getId().toString()
        );

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("findParentCommentsByBlogId_正常系_複数の返信数を確認")
    void findParentCommentsByBlogId_正常系_複数の返信数を確認() {
        // 準備
        Comment replyComment2 = testDataSetup.createTestComment(testUser, testBlog, "Test reply comment 2");
        testDataSetup.createTestCommentTree(
                parentComment,
                replyComment2,
                2,
                testUser.getId().toString()
        );

        // 実行
        List<ParentCommentRepositoryDto> result = commentRepository.findParentCommentsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComment().getId()).isEqualTo(parentComment.getId());
        assertThat(result.get(0).getReplyCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("findParentCommentsByBlogId_正常系_作成日時の降順でソート")
    void findParentCommentsByBlogId_正常系_作成日時の降順でソート() {
        // 準備
        LocalDateTime laterTime = LocalDateTime.now().plusHours(1);
        Comment laterParentComment = Comment.builder()
                .content("Later parent comment")
                .author(testUser)
                .blog(testBlog)
                .commentCreatedTime(laterTime)
                .commentUpdatedTime(laterTime)
                .isDeleted(false)
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .build();
        commentRepository.save(laterParentComment);
        entityManager.flush();

        // 実行
        List<ParentCommentRepositoryDto> result = commentRepository.findParentCommentsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getComment().getId()).isEqualTo(laterParentComment.getId());
        assertThat(result.get(1).getComment().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    @DisplayName("updateCommentContent_正常系_異なるユーザーによる更新")
    void updateCommentContent_正常系_異なるユーザーによる更新() {
        // 準備
        User otherUser = testDataSetup.createTestUser();
        String newContent = "Updated by other user";
        LocalDateTime updateTime = LocalDateTime.now();

        // 実行
        commentRepository.updateCommentContent(
                parentComment.getId(),
                newContent,
                updateTime,
                otherUser.getId().toString()
        );
        entityManager.flush();
        entityManager.refresh(parentComment);

        // 検証
        Comment result = commentRepository.findById(parentComment.getId()).orElseThrow();
        assertThat(result.getContent()).isEqualTo(newContent);
        assertThat(result.getUpdatedBy()).isEqualTo(otherUser.getId().toString());
    }

    @Test
    @DisplayName("findById_正常系_コメントが存在する場合")
    void findById_正常系_コメントが存在する場合() {
        // 実行
        Optional<Comment> result = commentRepository.findById(testComment.getId());

        // 検証
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testComment.getId());
        assertThat(result.get().getContent()).isEqualTo("テストコメント");
        assertThat(result.get().getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("findById_正常系_コメントが存在しない場合")
    void findById_正常系_コメントが存在しない場合() {
        // 実行
        Optional<Comment> result = commentRepository.findById(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById_正常系_論理削除されたコメントは取得できない")
    void findById_正常系_論理削除されたコメントは取得できない() {
        // 実行
        Optional<Comment> result = commentRepository.findById(deletedComment.getId());

        // 検証
        assertThat(result).isEmpty();
    }
} 