package com.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.repository.jpa.NotificationRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class NotificationServiceTest extends SpringBootTestBase {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User currentUser;
    private User otherUser;
    private Notification followNotification;
    private Notification commentNotification;
    private Notification blogCreatedNotification;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        currentUser = testDataSetup.createUser("test-user", "Test User");
        otherUser = testDataSetup.createUser("other-user", "Other User");

        // 認証情報の設定
        testDataSetup.setupAuthentication(currentUser);

        // ブログとコメントの準備（通知の関連データとして使用）
        Blog testBlog = testDataSetup.createTestBlog(otherUser);
        Comment testComment = testDataSetup.createTestComment(otherUser, testBlog, "Test comment");

        // 通知データの準備
        followNotification = testDataSetup.createTestNotification(
            currentUser,
            otherUser,
            NotificationType.FOLLOW,
            null,
            null,
            false
        );

        commentNotification = testDataSetup.createTestNotification(
            currentUser,
            otherUser,
            NotificationType.COMMENT,
            testBlog,
            testComment,
            false
        );

        blogCreatedNotification = testDataSetup.createTestNotification(
            currentUser,
            otherUser,
            NotificationType.BLOG_CREATED,
            testBlog,
            null,
            false
        );
    }

    @Test
    void countUnreadNotifications_正常系() {
        // 未読通知の件数を取得
        Long count = notificationService.countUnreadNotifications();

        // 検証
        assertThat(count).isEqualTo(3L); // 3つの未読通知が存在する
    }

    @Test
    void getNotificationsForCurrentUser_正常系() {
        // 未読通知を取得
        List<Notification> notifications = notificationService.getNotificationsForCurrentUser();

        // 検証
        assertThat(notifications).hasSize(3);
        assertThat(notifications).containsExactlyInAnyOrder(
            followNotification,
            commentNotification,
            blogCreatedNotification
        );
    }

    @Test
    void getAllNotificationsForCurrentUser_正常系() {
        // 既読の通知を追加
        Notification readNotification = testDataSetup.createTestNotification(
            currentUser,
            otherUser,
            NotificationType.FOLLOW,
            null,
            null,
            true
        );

        // 全通知を取得
        List<Notification> notifications = notificationService.getAllNotificationsForCurrentUser();

        // 検証
        assertThat(notifications).hasSize(4);
        assertThat(notifications).containsExactlyInAnyOrder(
            followNotification,
            commentNotification,
            blogCreatedNotification,
            readNotification
        );
    }

    @Test
    void getUnreadFollowNotifications_正常系() {
        // フォロー通知を取得
        List<Notification> notifications = notificationService.getUnreadFollowNotifications(
            currentUser.getId(),
            otherUser.getId()
        );

        // 検証
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0)).isEqualTo(followNotification);
    }

    @Test
    void getUnreadNotificationsByCommentId_正常系() {
        // コメント通知を取得
        List<Notification> notifications = notificationService.getUnreadNotificationsByCommentId(
            commentNotification.getRelatedComment().getId()
        );

        // 検証
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0)).isEqualTo(commentNotification);
    }

    @Test
    void getUnreadBlogCreatedNotificationsByBlogId_正常系() {
        // ブログ作成通知を取得
        List<Notification> notifications = notificationService.getUnreadBlogCreatedNotificationsByBlogId(
            blogCreatedNotification.getRelatedBlog().getId()
        );

        // 検証
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0)).isEqualTo(blogCreatedNotification);
    }

    @Test
    void markNotificationsAsRead_正常系() {
        // 通知を既読にする
        notificationService.markNotificationsAsRead(Arrays.asList(
            followNotification.getId(),
            commentNotification.getId()
        ));
        entityManager.flush();
        entityManager.refresh(followNotification);
        entityManager.refresh(commentNotification);

        // 検証
        List<Notification> updatedNotifications = notificationRepository.findAllById(
            Arrays.asList(followNotification.getId(), commentNotification.getId())
        );
        assertThat(updatedNotifications).allMatch(n -> n.getIsRead());
        assertThat(updatedNotifications).allMatch(n -> n.getReadAt() != null);
    }

    @Test
    void saveNotification_正常系() {
        // 新しい通知を作成
        Notification newNotification = Notification.builder()
                .targetUser(currentUser)
                .triggerUser(otherUser)
                .notificationType(NotificationType.FOLLOW)
                .notificationCreatedAt(LocalDateTime.now())
                .isRead(false)
                .isDeleted(false)
                .createdBy("Test")
                .updatedBy("Test")
                .build();

        // 通知を保存
        Notification savedNotification = notificationService.saveNotification(newNotification);

        // 検証
        assertThat(savedNotification.getId()).isNotNull();
        assertThat(savedNotification.getTargetUser()).isEqualTo(currentUser);
        assertThat(savedNotification.getTriggerUser()).isEqualTo(otherUser);
        assertThat(savedNotification.getNotificationType()).isEqualTo(NotificationType.FOLLOW);
    }

    @Test
    void deleteNotification_正常系() {
        // 通知を削除
        notificationService.deleteNotification(followNotification.getId());
        entityManager.flush();
        entityManager.refresh(followNotification);
        // 検証
        Notification deletedNotification = notificationRepository.findById(followNotification.getId()).orElse(null);
        assertThat(deletedNotification).isNotNull();
        assertThat(deletedNotification.getIsDeleted()).isTrue();
        assertThat(deletedNotification.getUpdatedBy()).isEqualTo(currentUser.getId().toString());
    }

    @Test
    void deleteNotifications_正常系() {
        // 複数の通知を削除
        notificationService.deleteNotifications(Arrays.asList(
            followNotification.getId(),
            commentNotification.getId()
        ));
        entityManager.flush();
        entityManager.refresh(followNotification);
        entityManager.refresh(commentNotification);

        // 検証
        List<Notification> deletedNotifications = notificationRepository.findAllById(
            Arrays.asList(followNotification.getId(), commentNotification.getId())
        );
        assertThat(deletedNotifications).allMatch(n -> n.getIsDeleted());
        assertThat(deletedNotifications).allMatch(n -> n.getUpdatedBy().equals(currentUser.getId().toString()));
    }

    @Test
    void markAllUnreadNotificationsAsRead_正常系() {
        // すべての未読通知を既読にする
        notificationService.markAllUnreadNotificationsAsRead(currentUser.getId());

        // 検証
        List<Notification> updatedNotifications = notificationService.getNotificationsForCurrentUser();
        assertThat(updatedNotifications).isEmpty();
    }
} 