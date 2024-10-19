package com.example.bookstore.dto.repository;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * ユーザプロフィールRepositoryDto
 */
@Data
@AllArgsConstructor
@Builder
public class ProfileRepositoryDto {
    /**
     * ユーザID
     */
    private Long userId;

    /**
     * ユーザ名
     */
    private String displayName;

    /**
     * プロフィール画像
     */
    private String profileImageUrl;

    /**
     * 自己紹介
     */
    private String selfIntroduction;

    /**
     * 好きなアーティストリスト
     */
    private List<Artist> favoriteArtistList;

    /**
     * ブログリスト
     */
    private List<Blog> blogList;

}
