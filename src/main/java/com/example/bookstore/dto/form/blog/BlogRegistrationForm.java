package com.example.bookstore.dto.form.blog;

import com.example.bookstore.entity.code.BlogCategory;
import com.example.bookstore.entity.code.BlogStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ブログ情報登録Form
 */
@Data
@Builder
public class BlogRegistrationForm {

    /**
     * 記事のタイトル
     */
    private String title;

    /**
     * 記事のコンテンツ (リッチテキスト / HTML)
     */
    private String content;

    /**
     * 記事のステータス (下書き、公開済み、アーカイブ)
     */
    private BlogStatus status;

    /**
     * タグ (カンマ区切りの文字列、または別エンティティにマッピング)
     */
    private String tags;

    /**
     * サムネイル画像のURL
     */
    private String thumbnailUrl;

    /**
     * スラッグ (URLフレンドリーな記事の識別子)
     */
    private String slug;

    /**
     * 記事のカテゴリ (オプション)
     */
    private BlogCategory category;

    /**
     * セットリスト
     */
    private String setlist;

    /**
     * アーティストIDリスト
     */
    private List<String> artistIdList;

}
