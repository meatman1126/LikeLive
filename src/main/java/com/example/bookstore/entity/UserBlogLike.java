package com.example.bookstore.entity;

import com.example.bookstore.entity.key.UserBlogLikeId;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ユーザブログリレーションエンティティ
 * ブログに対していいねをしたユーザを紐付ける
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "user_blog_like")
public class UserBlogLike extends BaseEntity {

    @EmbeddedId
    private UserBlogLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blogId")
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

}