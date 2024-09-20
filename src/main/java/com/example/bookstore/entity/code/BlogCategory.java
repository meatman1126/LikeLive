package com.example.bookstore.entity.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlogCategory implements BaseEnum<BlogCategory> {
    DIARY(1, "Diary"),
    REPORT(2, "Report"),
    OTHER(3, "Other");
    // 通し番号フィールド
    private final int code;

    // ステータス名フィールド
    private final String description;


}
