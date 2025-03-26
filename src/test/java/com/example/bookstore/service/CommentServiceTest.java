package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.dto.view.ParentCommentViewDto;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.key.CommentTreeId;
import com.example.bookstore.exception.CommentNotFoundException;
import com.example.bookstore.repository.jpa.CommentRepository;
import com.example.bookstore.repository.jpa.CommentTreeRepository;
import com.example.bookstore.repository.jpa.NotificationRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CommentServiceTest extends SpringBootTestBase {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentTreeRepository commentTreeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Blog testBlog;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        // テストユーザーを作成し、認証情報を設定
        testUser = createUserAndSetupAuthentication(testDataSetup);

        // テストブログを作成
        testBlog = testDataSetup.createTestBlog(testUser);

        // テストコメントを作成
        testComment = testDataSetup.createTestComment(testUser, testBlog, "Test comment");
    }

    @Test @DisplayName("ブログIDでコメントを取得できる")
    void getCommentsByBlogId_ReturnsComments() {
        // 追加のコメントを作成
        Comment comment2 = testDataSetup.createTestComment(testUser, testBlog, "Test comment 2");
        Comment comment3 = testDataSetup.createTestComment(testUser, testBlog, "Test comment 3");

        // 実行
        List<ParentCommentViewDto> results = commentService.getCommentsByBlogId(testBlog.getId());

        // 検証
        assertNotNull(results);
        assertEquals(3, results.size());

        // 各コメントの内容を検証
        assertTrue(results.stream().anyMatch(c -> c.getComment().getId().equals(testComment.getId())));
        assertTrue(results.stream().anyMatch(c -> c.getComment().getId().equals(comment2.getId())));
        assertTrue(results.stream().anyMatch(c -> c.getComment().getId().equals(comment3.getId())));
    }

    @Test @DisplayName("存在しないブログIDでコメントを取得すると空のリストが返される")
    void getCommentsByBlogId_WithNonExistentBlogId_ReturnsEmptyList() {
        // 実行
        List<ParentCommentViewDto> results = commentService.getCommentsByBlogId(999999L);

        // 検証
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test @DisplayName("親コメントIDで返信コメントを取得できる")
    void getCommentsAndRepliesByParentId_ReturnsReplies() {
        // 返信コメントを作成
        Comment reply1 = testDataSetup.createTestReplyComment(testUser, testBlog, testComment);
        Comment reply2 = testDataSetup.createTestReplyComment(testUser, testBlog, testComment);

        // 実行
        List<Comment> results = commentService.getCommentsAndRepliesByParentId(testComment.getId());

        // 検証
        assertNotNull(results);
        assertEquals(2, results.size());

        // 各返信コメントの内容を検証
        assertTrue(results.stream().anyMatch(c -> c.getId().equals(reply1.getId())));
        assertTrue(results.stream().anyMatch(c -> c.getId().equals(reply2.getId())));
    }

    @Test @DisplayName("存在しない親コメントIDで返信を取得すると空のリストが返される")
    void getCommentsAndRepliesByParentId_WithNonExistentParentId_ReturnsEmptyList() {
        // 実行
        List<Comment> results = commentService.getCommentsAndRepliesByParentId(999999L);

        // 検証
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test @DisplayName("コメントを新規登録できる")
    void registerComment_CreatesCommentAndNotification() {
        // 新規コメントの準備
        Comment newComment = Comment.builder()
                .content("新規コメントの内容")
                .blog(testBlog)
                .author(testUser)
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .isDeleted(false)
                .build();

        // 実行
        Comment result = commentService.registerComment(newComment);

        // 検証
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("新規コメントの内容", result.getContent());
        assertEquals(testUser.getId(), result.getAuthor().getId());
        assertEquals(testBlog.getId(), result.getBlog().getId());

        // 通知が作成されていることを確認
        List<Notification> notifications = notificationRepository.findUnreadNotificationsByCommentId(result.getId());
        assertFalse(notifications.isEmpty());
        assertEquals(testBlog.getAuthor().getId(), notifications.get(0).getTargetUser().getId());
    }

    @Test @DisplayName("返信コメントを登録できる")
    void registerReplyComment_CreatesReplyAndCommentTree() {
        // 返信コメントの準備
        Comment replyComment = Comment.builder()
                .content("返信コメントの内容")
                .blog(testBlog)
                .author(testUser)
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .isDeleted(false)
                .build();

        // 実行
        Comment result = commentService.registerReplyComment(testComment.getId(), replyComment);

        // 検証
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("返信コメントの内容", result.getContent());
        assertEquals(testUser.getId(), result.getAuthor().getId());
        assertEquals(testBlog.getId(), result.getBlog().getId());

        // CommentTreeが作成されていることを確認
        var commentTree = commentTreeRepository.findById(new CommentTreeId(testComment.getId(), result.getId()));
        assertTrue(commentTree.isPresent());
        assertEquals(1, commentTree.get().getReplyNumber());
    }

    @Test @DisplayName("コメントを更新できる")
    void updateComment_UpdatesContent() {
        // 更新用の内容
        String updatedContent = "更新後のコメント内容";

        // 実行
        Comment result = commentService.updateComment(testComment.getId(), updatedContent);
        entityManager.flush();
        entityManager.refresh(testComment);
        

        // 検証
        assertNotNull(result);
        assertEquals(updatedContent, result.getContent());
        assertEquals(testUser.getId(), result.getAuthor().getId());
        assertEquals(testBlog.getId(), result.getBlog().getId());
    }

    @Test @DisplayName("コメントを削除できる")
    void deleteComment_DeletesCommentAndNotification() {
        // 実行
        Boolean result = commentService.deleteComment(testComment.getId());

        // 検証
        assertTrue(result);
        
        // コメントが論理削除されていることを確認
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.getCommentById(testComment.getId());
        });

        // 通知が削除されていることを確認
        List<Notification> notifications = notificationRepository.findUnreadNotificationsByCommentId(testComment.getId());
        assertTrue(notifications.isEmpty());
    }

    @Test @DisplayName("存在しないコメントIDで削除を試みるとfalseが返される")
    void deleteComment_WithNonExistentId_ReturnsFalse() {
        // 実行
        Boolean result = commentService.deleteComment(999999L);

        // 検証
        assertFalse(result);
    }

    @Test @DisplayName("コメントIDでコメントを取得できる")
    void getCommentById_ReturnsComment() {
        // 実行
        Comment result = commentService.getCommentById(testComment.getId());

        // 検証
        assertNotNull(result);
        assertEquals(testComment.getId(), result.getId());
        assertEquals(testComment.getContent(), result.getContent());
        assertEquals(testUser.getId(), result.getAuthor().getId());
        assertEquals(testBlog.getId(), result.getBlog().getId());
    }

    @Test @DisplayName("存在しないコメントIDで取得を試みるとnullが返される")
    void getCommentById_WithNonExistentId_ReturnsNull() {
        // 実行
        // 存在しないコメントIDで取得を試みるとCommentNotFoundExceptionがスローされる
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.getCommentById(999999L);
        });

    }

    @Test @DisplayName("存在しない親コメントIDで返信を登録するとCommentNotFoundExceptionがスローされる")
    void registerReplyComment_WithNonExistentParentId_ThrowsCommentNotFoundException() {
        // 返信コメントの準備
        Comment replyComment = Comment.builder()
                .content("返信コメントの内容")
                .blog(testBlog)
                .author(testUser)
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .isDeleted(false)
                .build();

        // 実行と検証
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.registerReplyComment(999999L, replyComment);
        });
    }

    @Test @DisplayName("存在しないコメントIDで更新を試みるとCommentNotFoundExceptionがスローされる")
    void updateComment_WithNonExistentId_ThrowsCommentNotFoundException() {
        // 実行と検証
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.updateComment(999999L, "更新後のコメント内容");
        });
    }

    @Test @DisplayName("存在しないコメントIDで取得を試みるとCommentNotFoundExceptionがスローされる")
    void getCommentById_WithNonExistentId_ThrowsCommentNotFoundException() {
        // 実行と検証
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.getCommentById(999999L);
        });
    }

    @Test @DisplayName("論理削除されたコメントIDで取得を試みるとCommentNotFoundExceptionがスローされる")
    void getCommentById_WithDeletedCommentId_ThrowsCommentNotFoundException() {
        // コメントを論理削除
        commentService.deleteComment(testComment.getId());

        // 実行と検証
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.getCommentById(testComment.getId());
        });
    }
} 