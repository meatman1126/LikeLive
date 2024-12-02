package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * ユーザエンティティクラス
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@SuperBuilder
@Data
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 表示名
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * 利用可否 利用可能ユーザの場合true
     */
    @Column(nullable = false)
    private Boolean enabled;

    /**
     * Idpにより提供される一意なキー
     */
    @Column
    private String subject;

    /**
     * ユーザの自己紹介文
     */
    @Column(name = "self_introduction", length = 1000)
    private String selfIntroduction;

    /**
     * プロフィール画像のURL
     */
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    /**
     * ユーザアーティストリレーション
     */
    @OneToMany(mappedBy = "user")
    private Set<UserArtist> userArtists;

    /**
     * ユーザブログリレーション（ユーザがいいねをしたブログデータを保持）
     */
    @OneToMany(mappedBy = "user")
    private Set<UserBlogLike> likedBlogs;


}
