package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * アーティストエンティティクラス
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "artists")
@SuperBuilder
@Data
@NoArgsConstructor
public class Artist extends BaseEntity {

    /**
     * アーティストID（Spotify側で定義されている一意の文字列）
     */
    @Id
    private String id;

    /**
     * アーティスト名
     */
    @Column
    private String name;

    /**
     * アーティスト画像URL
     */
    @Column
    private String imageUrl;

    /**
     * ユーザアーティストリレーション
     */
    @OneToMany(mappedBy = "artist")
    private Set<UserArtist> userArtists;

    /**
     * ブログアーティストリレーション
     */
    @OneToMany(mappedBy = "artist")
    private Set<BlogArtist> blogArtists;


}
