package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 指定したユーザがフォローしているユーザリストを取得します。
     *
     * @param userId ユーザID
     * @return 指定したユーザがフォローしているユーザリスト
     */
    @Query("SELECT f.followed FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowedUsers(@Param("userId") Long userId);

    /**
     * 指定したユーザをフォローしているユーザリストを取得します。
     *
     * @param userId ユーザID
     * @return 指定したユーザをフォローしているユーザリスト
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.followed.id = :userId")
    List<User> findFollowers(@Param("userId") Long userId);

    /**
     * ユーザ間のフォロー関係を削除します。
     *
     * @param followerId フォローしているユーザ
     * @param followedId フォローされているユーザ
     */
    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId")
    void unfollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}
