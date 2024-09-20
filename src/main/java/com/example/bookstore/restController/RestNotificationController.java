package com.example.bookstore.restController;

import com.example.bookstore.dto.form.notification.NotificationRegistrationForm;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.service.BlogService;
import com.example.bookstore.service.NotificationService;
import com.example.bookstore.service.UserService;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class RestNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtilService userUtilService;

    @Autowired
    private BlogService blogService;

    /**
     * ログインユーザの未読通知件数を取得します。
     *
     * @return 未読通知件数
     */
    @GetMapping("/notification/unread/count")
    public ResponseEntity<Long> countUnreadNotifications() {
        Long notificationsCount = notificationService.countUnreadNotifications();
        return ResponseEntity.ok(notificationsCount);
    }

    /**
     * ログインユーザの未読通知を取得します。
     *
     * @return 未読通知リスト
     */
    @GetMapping("/notification/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        List<Notification> unreadNotifications = notificationService.getNotificationsForCurrentUser();
        return ResponseEntity.ok(unreadNotifications);
    }

    /**
     * ログインユーザの全通知を取得します。
     *
     * @return 全通知リスト
     */
    @GetMapping("/notification/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> allNotifications = notificationService.getAllNotificationsForCurrentUser();
        return ResponseEntity.ok(allNotifications);
    }

    /**
     * 指定された通知IDリストを既読としてマークします。
     *
     * @param notificationIds 既読にする通知IDリスト
     * @return 更新のステータス
     */
    @PostMapping("/notification/mark-read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 新しい通知を作成します。
     *
     * @param form 保存する通知
     * @return 作成された通知
     */
    @PostMapping("/notification/create")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRegistrationForm form) {
        Blog relatedBlog = null;
        if (form.getNotificationType() == NotificationType.COMMENT
                || form.getNotificationType() == NotificationType.BLOG_CREATED) {
            relatedBlog = blogService.findById(form.getRelatedBlogId());
        }
        User currentUser = userUtilService.getCurrentUser();
        Notification createdNotification = Notification.builder()
                .targetUser(userService.getUserInfo(form.getTargetUserId()))
                .notificationType(form.getNotificationType())
                .relatedBlog(relatedBlog)
                .notificationCreatedAt(LocalDateTime.now())
                .isRead(false)
                .triggerUser(currentUser)
                .createdBy(currentUser.getId().toString())
                .updatedBy(currentUser.getId().toString())
                .build();
        return ResponseEntity.ok(createdNotification);
    }
}