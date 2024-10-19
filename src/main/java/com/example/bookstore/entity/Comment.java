package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * コメントエンティティクラス
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comments")
@SuperBuilder
@Data
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * コメントのコンテンツ
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * コメントの作成者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * コメント対象のブログ記事
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    /**
     * コメントの作成日時
     */
    @Column(name = "comment_created_time", nullable = false, updatable = false)
    private LocalDateTime commentCreatedTime;

    /**
     * コメントの更新日時
     */
    @Column(name = "comment_updated_time", nullable = false)
    private LocalDateTime commentUpdatedTime;

    /**
     * 削除フラグ
     */
    @Column(nullable = false)
    private Boolean isDeleted = false;

}