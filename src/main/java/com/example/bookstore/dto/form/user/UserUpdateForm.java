package com.example.bookstore.dto.form.user;

import com.example.bookstore.entity.Artist;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ユーザ情報更新Form
 */
@Data
@Builder
public class UserUpdateForm {

    /**
     * ユーザ表示名
     */
    private String displayName;

    /**
     * 自己紹介文
     */
    private String selfIntroduction;

    private String profileImagePath;

    /**
     * アーティストリスト
     */
    private List<Artist> favoriteArtistList;


}
