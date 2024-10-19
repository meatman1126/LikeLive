package com.example.bookstore.entity.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ブログカテゴリEnum
 */
@Getter
@AllArgsConstructor
public enum BlogCategory implements BaseEnum<BlogCategory> {
    /**
     * 日記
     */
    DIARY(1, "DIARY"),
    /**
     * ライブレポ
     */
    REPORT(2, "REPORT"),
    /**
     * その他
     */
    OTHER(3, "OTHER");

    private final int code;

    private final String description;

    /**
     * JSONからjavaオブジェクトに変換する際に使用されるメソッド
     * JSONでは対象のdescriptionが指定されることを想定します。
     *
     * @param value JSONで指定されたdescription
     * @return 指定されたdescriptionに合致するBlogCategoryオブジェクト
     */
    @JsonCreator
    public static BlogCategory fromValue(String value) {
        for (BlogCategory category : values()) {
            if (category.description.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown BlogCategory: " + value);
    }

    /**
     * javaオブジェクトからJSONに変換する際に使用されるメソッド
     * JSONの値はdescriptionが使用されます。
     *
     * @return description
     */
    @JsonValue
    public String getDescription() {
        return this.description;
    }


}
