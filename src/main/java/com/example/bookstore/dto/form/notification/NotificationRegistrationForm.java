package com.example.bookstore.dto.form.notification;

import com.example.bookstore.entity.code.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRegistrationForm {

    /**
     * 通知対象ユーザID
     */
    private Long targetUserId;

    /**
     * 通知の種類
     */
    private NotificationType notificationType;

    /**
     * 通知に関連するブログID (ブログへのコメント通知、ブログ作成通知の場合)
     */
    private Long relatedBlogId;


}
