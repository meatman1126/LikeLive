package com.example.bookstore.dto.form.user;

import lombok.Builder;
import lombok.Data;

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

    /**
     * プロフィール画像URL
     */
    private String profileImageUrl;

    
}
