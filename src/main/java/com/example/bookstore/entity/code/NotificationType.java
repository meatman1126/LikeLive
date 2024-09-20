package com.example.bookstore.entity.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType implements BaseEnum<NotificationType> {

    /**
     * 他ユーザからのフォロー時
     */
    FOLLOW(1, "Follow"),

    /**
     * 自身のブログへのコメント
     */
    COMMENT(2, "Comment"),

    /**
     * フォロー中ユーザのブログが新規公開された場合
     */
    BLOG_CREATED(3, "BlogCreated");

    // 通し番号フィールド
    private final int code;

    // ステータス名フィールド
    private final String type;

}
