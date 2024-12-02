package com.example.bookstore.entity.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知タイプEnum
 */
@Getter
@AllArgsConstructor
public enum NotificationType implements BaseEnum<NotificationType> {

    /**
     * 他ユーザからのフォロー時
     */
    FOLLOW(1, "FOLLOW"),

    /**
     * 自身のブログへのコメント
     */
    COMMENT(2, "COMMENT"),

    /**
     * フォロー中ユーザのブログが新規公開された場合
     */
    BLOG_CREATED(3, "BLOG_CREATED");

    // 通し番号フィールド
    private final int code;

    // ステータス名フィールド
    private final String description;

}
