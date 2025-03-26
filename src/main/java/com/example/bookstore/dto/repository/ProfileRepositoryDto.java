package com.example.bookstore.dto.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


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

    public ProfileRepositoryDto(Long userId, String displayName, String profileImageUrl, String selfIntroduction) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
        this.selfIntroduction = selfIntroduction;
        this.favoriteArtistList = new ArrayList<>();
        this.blogList = new ArrayList<>();
    }
}
