package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class NotificationRepositoryTest extends JpaTestBase {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User targetUser;
    private User triggerUser;
    private Blog testBlog;
    private Comment testComment;
    private Notification followNotification;
    private Notification commentNotification;
    private Notification blogNotification;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        targetUser = testDataSetup.createTestUser();
        triggerUser = testDataSetup.createTestUser();
        testBlog = testDataSetup.createTestBlog(triggerUser);
        testComment = testDataSetup.createTestComment(triggerUser, testBlog, "Test Comment");

        // 通知データの作成
        followNotification = testDataSetup.createTestNotification(
                targetUser, triggerUser, NotificationType.FOLLOW, null, null, false);
        commentNotification = testDataSetup.createTestNotification(
                targetUser, triggerUser, NotificationType.COMMENT, testBlog, testComment, false);
        blogNotification = testDataSetup.createTestNotification(
                targetUser, triggerUser, NotificationType.BLOG_CREATED, testBlog, null, false);
    }

    @Test
    @DisplayName("countUnreadNotifications_正常系_未読通知が存在する場合")
    void countUnreadNotifications_正常系_未読通知が存在する場合() {
        // 実行
        Long result = notificationRepository.countUnreadNotifications(targetUser.getId());

        // 検証
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("countUnreadNotifications_正常系_未読通知が存在しない場合")
    void countUnreadNotifications_正常系_未読通知が存在しない場合() {
        // 実行
        Long result = notificationRepository.countUnreadNotifications(999L);

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("findUnreadNotificationsByUserId_正常系_未読通知が存在する場合")
    void findUnreadNotificationsByUserId_正常系_未読通知が存在する場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadNotificationsByUserId(targetUser.getId());

        // 検証
        assertThat(result).hasSize(3);
        assertThat(result).extracting("notificationType")
            .containsExactlyInAnyOrder(NotificationType.FOLLOW, NotificationType.COMMENT, NotificationType.BLOG_CREATED);
    }

    @Test
    @DisplayName("findUnreadNotificationsByUserId_正常系_未読通知が存在しない場合")
    void findUnreadNotificationsByUserId_正常系_未読通知が存在しない場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadNotificationsByUserId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllNotificationsByUserId_正常系_通知が存在する場合")
    void findAllNotificationsByUserId_正常系_通知が存在する場合() {
        // 実行
        List<Notification> result = notificationRepository.findAllNotificationsByUserId(targetUser.getId());

        // 検証
        assertThat(result).hasSize(3);
        assertThat(result).extracting("notificationType")
            .containsExactlyInAnyOrder(NotificationType.FOLLOW, NotificationType.COMMENT, NotificationType.BLOG_CREATED);
    }

    @Test
    @DisplayName("findAllNotificationsByUserId_正常系_通知が存在しない場合")
    void findAllNotificationsByUserId_正常系_通知が存在しない場合() {
        // 実行
        List<Notification> result = notificationRepository.findAllNotificationsByUserId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findUnreadFollowNotifications_正常系_未読のフォロー通知が存在する場合")
    void findUnreadFollowNotifications_正常系_未読のフォロー通知が存在する場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadFollowNotifications(
                targetUser.getId(), triggerUser.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotificationType()).isEqualTo(NotificationType.FOLLOW);
    }

    @Test
    @DisplayName("findUnreadFollowNotifications_正常系_未読のフォロー通知が存在しない場合")
    void findUnreadFollowNotifications_正常系_未読のフォロー通知が存在しない場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadFollowNotifications(
                999L, 888L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findUnreadNotificationsByCommentId_正常系_未読のコメント通知が存在する場合")
    void findUnreadNotificationsByCommentId_正常系_未読のコメント通知が存在する場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadNotificationsByCommentId(testComment.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotificationType()).isEqualTo(NotificationType.COMMENT);
    }

    @Test
    @DisplayName("findUnreadNotificationsByCommentId_正常系_未読のコメント通知が存在しない場合")
    void findUnreadNotificationsByCommentId_正常系_未読のコメント通知が存在しない場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadNotificationsByCommentId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findUnreadBlogCreatedNotificationsByBlogId_正常系_未読のブログ作成通知が存在する場合")
    void findUnreadBlogCreatedNotificationsByBlogId_正常系_未読のブログ作成通知が存在する場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadBlogCreatedNotificationsByBlogId(testBlog.getId());

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotificationType()).isEqualTo(NotificationType.BLOG_CREATED);
    }

    @Test
    @DisplayName("findUnreadBlogCreatedNotificationsByBlogId_正常系_未読のブログ作成通知が存在しない場合")
    void findUnreadBlogCreatedNotificationsByBlogId_正常系_未読のブログ作成通知が存在しない場合() {
        // 実行
        List<Notification> result = notificationRepository.findUnreadBlogCreatedNotificationsByBlogId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteNotificationById_正常系_通知が存在する場合")
    void deleteNotificationById_正常系_通知が存在する場合() {
        // 実行
        notificationRepository.deleteNotificationById(followNotification.getId(), "test-user");
        entityManager.flush();

        // 検証
        List<Notification> result = notificationRepository.findAllNotificationsByUserId(targetUser.getId());
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").doesNotContain(followNotification.getId());
    }

    @Test
    @DisplayName("deleteNotificationsByIds_正常系_通知が存在する場合")
    void deleteNotificationsByIds_正常系_通知が存在する場合() {
        // 実行
        notificationRepository.deleteNotificationsByIds(
                List.of(followNotification.getId(), commentNotification.getId()), "test-user");
        entityManager.flush();

        // 検証
        List<Notification> result = notificationRepository.findAllNotificationsByUserId(targetUser.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(blogNotification.getId());
    }

    @Test
    @DisplayName("markNotificationsAsRead_正常系_通知が存在する場合")
    void markNotificationsAsRead_正常系_通知が存在する場合() {
        // 準備
        LocalDateTime readAt = LocalDateTime.now();

        // 実行
        notificationRepository.markNotificationsAsRead(
                List.of(followNotification.getId(), commentNotification.getId()), readAt, "test-user");
        entityManager.flush();

        // 検証
        List<Notification> result = notificationRepository.findUnreadNotificationsByUserId(targetUser.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(blogNotification.getId());
    }

    @Test
    @DisplayName("markAllUnreadNotifications_正常系_未読通知が存在する場合")
    void markAllUnreadNotifications_正常系_未読通知が存在する場合() {
        // 実行
        notificationRepository.markAllUnreadNotifications(targetUser.getId(), "test-user");
        entityManager.flush();

        // 検証
        List<Notification> result = notificationRepository.findUnreadNotificationsByUserId(targetUser.getId());
        assertThat(result).isEmpty();
    }
} 