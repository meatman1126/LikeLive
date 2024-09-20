package com.example.bookstore.service;

import com.example.bookstore.entity.Notification;
import com.example.bookstore.repository.jpa.NotificationRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserUtilService userUtilService;

    /**
     * ログインユーザの未読の通知件数を取得します。
     *
     * @return 未読の通知件数
     */
    public Long countUnreadNotifications() {
        return notificationRepository.countUnreadNotifications(userUtilService.getCurrentUser().getId());
    }


    /**
     * ログインユーザの未読通知を取得します。
     *
     * @return 未読通知リスト
     */
    public List<Notification> getNotificationsForCurrentUser() {
        return notificationRepository.findUnreadNotificationsByUserId(userUtilService.getCurrentUser().getId());
    }

    /**
     * ログインユーザの全通知を取得します。
     *
     * @return 全通知リスト
     */
    public List<Notification> getAllNotificationsForCurrentUser() {
        return notificationRepository.findAllNotificationsByUserId(userUtilService.getCurrentUser().getId());
    }


    /**
     * 指定された条件に合致する未読かつ論理削除されていない通知を取得します。
     *
     * @param targetUserId  通知対象ユーザのID
     * @param triggerUserId 通知のトリガーとなったユーザのID
     * @return 条件に合致する未読通知のリスト
     */
    public List<Notification> getUnreadFollowNotifications(Long targetUserId, Long triggerUserId) {
        return notificationRepository.findUnreadFollowNotifications(targetUserId, triggerUserId);
    }

    /**
     * コメントIDに基づいて未読通知を取得します。
     *
     * @param commentId コメントID
     * @return コメントに関連する通知のリスト
     */
    public List<Notification> getUnreadNotificationsByCommentId(Long commentId) {
        return notificationRepository.findUnreadNotificationsByCommentId(commentId);
    }

    /**
     * ブログIDに基づいて未読の「ブログ作成通知」を取得します。
     *
     * @param blogId ブログID
     * @return ブログ作成通知のリスト
     */
    public List<Notification> getUnreadBlogCreatedNotificationsByBlogId(Long blogId) {
        return notificationRepository.findUnreadBlogCreatedNotificationsByBlogId(blogId);
    }

    /**
     * 指定された通知IDリストを既読としてマークします。
     *
     * @param notificationIds 既読にする通知IDリスト
     */
    @Transactional
    public void markNotificationsAsRead(List<Long> notificationIds) {
        LocalDateTime readAt = LocalDateTime.now();
        notificationRepository.markNotificationsAsRead(notificationIds, readAt, userUtilService.getCurrentUserId());
    }

    /**
     * 通知を新規で保存します。
     *
     * @param notification 保存する通知情報
     * @return 保存された通知
     */
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * 単一の通知を論理削除します。
     *
     * @param notificationId 通知ID
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteNotificationById(notificationId, userUtilService.getCurrentUserId());
    }

    /**
     * 複数の通知を論理削除します。
     *
     * @param notificationIds 通知IDのリスト
     */
    @Transactional
    public void deleteNotifications(List<Long> notificationIds) {
        notificationRepository.deleteNotificationsByIds(notificationIds, userUtilService.getCurrentUserId());
    }
}