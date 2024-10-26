package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.UserBlogLike;
import com.example.bookstore.entity.key.UserBlogLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ユーザブログいいねリポジトリ
 */
public interface UserBlogLikeRepository extends JpaRepository<UserBlogLike, UserBlogLikeId> {

    /**
     * 指定したユーザが指定したブログへのいいね有無を取得します。
     *
     * @param userId ユーザID
     * @param blogId ブログID
     * @return ユーザが指定したブログにいいねしている場合true
     */
    @Query("SELECT CASE WHEN COUNT(ubl) > 0 THEN true ELSE false END " +
            "FROM UserBlogLike ubl " +
            "WHERE ubl.id.userId = :userId AND ubl.id.blogId = :blogId")
    boolean isLikeBlog(@Param("userId") Long userId, @Param("blogId") Long blogId);

    /**
     * 指定したブログのいいねを取り消します。
     *
     * @param userId いいねを取り消すユーザID
     * @param blogId いいねを取り消すブログID
     */
    @Modifying
    @Query("DELETE FROM UserBlogLike ubl " +
            "WHERE ubl.id.userId = :userId AND ubl.id.blogId = :blogId")
    void clearLikeBlog(@Param("userId") Long userId, @Param("blogId") Long blogId);
}