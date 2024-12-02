package com.example.bookstore.entity.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ブログステータスEnum
 */
@Getter
@AllArgsConstructor
public enum BlogStatus implements BaseEnum<BlogStatus> {
    /**
     * 下書き
     */
    DRAFT(1, "DRAFT"),
    /**
     * 公開済み
     */
    PUBLISHED(2, "PUBLISHED"),
    /**
     * アーカイブ
     */
    ARCHIVED(3, "ARCHIVED");

    private final int code;

    private final String description;

    /**
     * JSONからjavaオブジェクトに変換する際に使用されるメソッド
     * JSONでは対象のdescriptionが指定されることを想定します。
     *
     * @param value JSONで指定されたdescription
     * @return 指定されたdescriptionに合致するBlogStatusオブジェクト
     */
    @JsonCreator
    public static BlogStatus fromValue(String value) {
        for (BlogStatus status : values()) {
            if (status.description.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BlogStatus: " + value);
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
