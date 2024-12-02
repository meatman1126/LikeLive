package com.example.bookstore.dto.repository;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ダッシュボード用ブログリポジトリDTO
 */
@Data
@Builder
public class DashboardBlogRepositoryDto {

    /**
     * ブログID
     */
    private Long id;
    /**
     * ブログタイトル
     */
    private String blogTitle;
    /**
     * サムネイル画像
     */
    private String thumbnailUrl;
    /**
     * ブログ著者のプロフィール画像
     */
    private String profileImageUrl;
    /**
     * 著者名
     */
    private String authorName;
    /**
     * 著者のフォロー状況（ログインユーザが著者をフォローしている場合true）
     */
    private Boolean isFollowAuthor;
    /**
     * ブログ作成日時
     */
    private LocalDateTime blogCreatedTime;

}
