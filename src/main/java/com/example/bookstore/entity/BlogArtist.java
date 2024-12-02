package com.example.bookstore.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ブログとアーティストのリレーションエンティティ
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_artists")
@SuperBuilder
@Data
@NoArgsConstructor
public class BlogArtist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ブログエンティティ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    /**
     * アーティストエンティティ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
}