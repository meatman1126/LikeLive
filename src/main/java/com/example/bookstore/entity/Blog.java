package com.example.bookstore.entity;

import com.example.bookstore.dto.converter.ContentConverter;
import com.example.bookstore.dto.converter.SetlistConverter;
import com.example.bookstore.entity.code.BlogCategory;
import com.example.bookstore.entity.code.BlogStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * ブログエンティティクラス
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blogs")
@SuperBuilder
@Data
@NoArgsConstructor
public class Blog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 記事のタイトル
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 記事のコンテンツ (リッチテキスト / JSON)
     */
    @Convert(converter = ContentConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Map<String, Object> content;

    /**
     * 記事のステータス (下書き、公開済み、アーカイブ)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlogStatus status;

    /**
     * 作成者のユーザID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 記事の作成日時
     */
    @Column(name = "blog_created_time", nullable = false, updatable = false)
    private LocalDateTime blogCreatedTime;

    /**
     * 記事の更新日時
     */
    @Column(name = "blog_updated_time", nullable = false)
    private LocalDateTime blogUpdatedTime;

    /**
     * タグ (カンマ区切りの文字列、または別エンティティにマッピング)
     */
    @Column(length = 255)
    private String tags;

    /**
     * 閲覧数
     */
    @Column(name = "view_count")
    private int viewCount;

    /**
     * いいね数
     */
    @Column(name = "like_count")
    private int likeCount;

    /**
     * コメント数
     */
    @Column(name = "comment_count")
    private int commentCount;

    /**
     * サムネイル画像のURL
     */
    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    /**
     * スラッグ (URLフレンドリーな記事の識別子)
     */
    @Column(length = 255, unique = true)
    private String slug;

    /**
     * 記事のカテゴリ (オプション)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlogCategory category;


    /**
     * 削除フラグ
     */
    @Column(nullable = false)
    private Boolean isDeleted = false;

    /**
     * セットリスト
     */
    @Convert(converter = SetlistConverter.class)
    @Column(columnDefinition = "TEXT")
    private Setlist setlist;

    /**
     * ブログアーティストリレーション
     */
    @OneToMany(mappedBy = "blog")
    private Set<BlogArtist> blogArtists;

}
