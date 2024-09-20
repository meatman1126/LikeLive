package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 指定されたユーザの未読通知件数を取得します。
     *
     * @param userId ユーザID
     * @return 未読通知件数
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.targetUser.id = :userId AND n.isRead = false AND n.isDeleted = false")
    Long countUnreadNotifications(@Param("userId") Long userId);

    /**
     * 指定されたユーザに関連する未読通知を取得します（論理削除されていないデータ）。
     *
     * @param userId ユーザID
     * @return 未読通知のリスト
     */
    @Query("SELECT n FROM Notification n WHERE n.targetUser.id = :userId AND n.isRead = false AND n.isDeleted = false ORDER BY n.notificationCreatedAt DESC")
    List<Notification> findUnreadNotificationsByUserId(@Param("userId") Long userId);

    /**
     * 指定されたユーザに関連する全ての通知を取得します。
     *
     * @param userId ユーザID
     * @return 通知のリスト
     */
    @Query("SELECT n FROM Notification n WHERE n.targetUser.id = :userId AND n.isDeleted = false ORDER BY n.notificationCreatedAt DESC")
    List<Notification> findAllNotificationsByUserId(@Param("userId") Long userId);

    /**
     * 指定されたユーザに関連する未読のフォロー通知を取得します。
     *
     * @param targetUserId  通知対象のユーザID
     * @param triggerUserId 通知トリガーとなったユーザID
     * @return フォロー通知リスト
     */
    @Query("SELECT n FROM Notification n " +
            "WHERE n.targetUser.id = :targetUserId " +
            "AND n.triggerUser.id = :triggerUserId " +
            "AND n.notificationType = 'FOLLOW' " +
            "AND n.isRead = false " +
            "AND n.isDeleted = false")
    List<Notification> findUnreadFollowNotifications(
            @Param("targetUserId") Long targetUserId,
            @Param("triggerUserId") Long triggerUserId
    );

    /**
     * コメントIDに合致し、NotificationTypeがコメント通知であり、未読である通知データを取得します。
     *
     * @param commentId コメントID
     * @return 合致する未読のコメント通知のリスト
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedComment.id = :commentId " +
            "AND n.notificationType = 'COMMENT' AND n.isRead = false AND n.isDeleted = false")
    List<Notification> findUnreadNotificationsByCommentId(@Param("commentId") Long commentId);

    /**
     * ブログIDに合致する未読の「ブログ作成通知」データを取得します。
     *
     * @param blogId ブログID
     * @return 合致する未読の「ブログ作成通知」のリスト
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedBlog.id = :blogId " +
            "AND n.notificationType = 'BLOG_CREATED' AND n.isRead = false AND n.isDeleted = false")
    List<Notification> findUnreadBlogCreatedNotificationsByBlogId(@Param("blogId") Long blogId);

    /**
     * 単一の通知を論理削除します。
     *
     * @param notificationId 通知ID
     * @param userId         更新ユーザID
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.updatedBy = :userId WHERE n.id = :notificationId")
    void deleteNotificationById(@Param("notificationId") Long notificationId, @Param("userId") String userId);

    /**
     * 複数の通知を論理削除します。
     *
     * @param notificationIds 通知IDのリスト
     * @param userId          更新ユーザID
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.updatedBy = :userId WHERE n.id IN :notificationIds")
    void deleteNotificationsByIds(@Param("notificationIds") List<Long> notificationIds, @Param("userId") String userId);

    /**
     * 通知を既読としてマークします。
     *
     * @param notificationIds 通知IDのリスト
     * @param readAt          既読にした日時
     * @param userId          更新ユーザID
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt, n.updatedBy = :userId " +
            "WHERE n.id IN :notificationIds AND n.isDeleted = false")
    void markNotificationsAsRead(
            @Param("notificationIds") List<Long> notificationIds,
            @Param("readAt") LocalDateTime readAt,
            @Param("userId") String userId
    );
}