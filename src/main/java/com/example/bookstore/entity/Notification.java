package com.example.bookstore.entity;

import com.example.bookstore.entity.code.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 通知エンティティクラス
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications")
@SuperBuilder
@Data
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 通知対象ユーザ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    /**
     * 通知のトリガーとなったアクションを行ったユーザ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_user_id")
    private User triggerUser;
    /**
     * 通知の種類
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    /**
     * 通知が既読か未読か
     */
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    /**
     * 通知の作成日時
     */
    @Column(name = "notification_created_at", nullable = false, updatable = false)
    private LocalDateTime notificationCreatedAt;

    /**
     * 通知が既読になった日時
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 通知に関連するブログ (ブログへのコメント通知、ブログ作成通知の場合)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog relatedBlog;

    /**
     * 通知に関連するコメント (ブログへのコメント通知の場合)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment relatedComment;

    /**
     * 削除フラグ
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

}