package com.example.bookstore.dto.repository;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ダッシュボードリポジトリDTO
 */
@Data
@Builder
//コンストラクタないのにDB結果をマッピングできている、なぜなのか？
public class DashboardRepositoryDto {

    /**
     * ブログID
     */
    private Long id;
    /**
     * ブログタイトル
     */
    private String blogTitle;
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
