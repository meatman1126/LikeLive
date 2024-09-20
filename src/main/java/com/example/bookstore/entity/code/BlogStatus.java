package com.example.bookstore.entity.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 記事のステータスを表す列挙型
 */
@Getter
@AllArgsConstructor
public enum BlogStatus implements BaseEnum<BlogStatus> {
    DRAFT(1, "Draft"),
    PUBLISHED(2, "Published"),
    ARCHIVED(3, "Archived");

    // 通し番号フィールド
    private final int code;

    // ステータス名フィールド
    private final String description;
}
