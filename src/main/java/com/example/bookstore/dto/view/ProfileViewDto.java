package com.example.bookstore.dto.view;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ユーザプロフィールViewDto
 */
@Data
@Builder
public class ProfileViewDto {
    /**
     * ユーザID
     */
    private Long userId;

    /**
     * ユーザ名
     */
    private String displayName;

    /**
     * プロフィール名
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
    private List<Blog> createdBlogList;

    /**
     * フォロー中のユーザ数
     */
    private Long followedCount;

    /**
     * フォロワー数
     */
    private Long followerCount;

    /**
     * RepostitoryDtoからViewDtoに変換
     */
    public static ProfileViewDto build(User userInfo, List<Artist> favoriteArtistList, List<Blog> createdBLogList, Long followedCount, Long followerCount) {
        return ProfileViewDto.builder()
                .userId(userInfo.getId())
                .displayName(userInfo.getDisplayName())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .selfIntroduction(userInfo.getSelfIntroduction())
                .favoriteArtistList(favoriteArtistList)
                .createdBlogList(createdBLogList)
                .followedCount(followedCount)
                .followerCount(followerCount)
                .build();
    }

}
