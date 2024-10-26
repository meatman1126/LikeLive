package com.example.bookstore.repository.jpa;

import com.example.bookstore.dto.repository.FollowRepositoryDto;
import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * フォローリポジトリインターフェース
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 指定したユーザのフォロワーリストを取得します。
     *
     * @param userId ユーザID
     * @return 指定したユーザのフォロワーリスト
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.followed.id = :userId")
    List<User> findFollowers(@Param("userId") Long userId);

    /**
     * 指定したユーザがフォロー中のユーザ一覧を取得します。
     * 戻り値のisFollowingは固定でtrueです。
     *
     * @param userId ユーザID
     * @return フォロー中のユーザリストとisFollowingの状態
     */
    @Query("SELECT new com.example.bookstore.dto.repository.FollowRepositoryDto(f.followed, true) " +
            "FROM Follow f WHERE f.follower.id = :userId")
    List<FollowRepositoryDto> findFollowedUsersInfo(@Param("userId") Long userId);

    /**
     * 指定したユーザのフォロワー一覧を取得します。
     * isFollowingは、指定したユーザがフォロワーをフォローしているかを確認します。
     *
     * @param userId ユーザID
     * @return フォロワーリストとisFollowingの状態
     */
    @Query("SELECT new com.example.bookstore.dto.repository.FollowRepositoryDto(f.follower, " +
            "CASE WHEN (SELECT COUNT(f2) FROM Follow f2 WHERE f2.follower.id = :userId AND f2.followed.id = f.follower.id) > 0 THEN true ELSE false END) " +
            "FROM Follow f WHERE f.followed.id = :userId")
    List<FollowRepositoryDto> findFollowersInfo(@Param("userId") Long userId);

    /**
     * 指定した2ユーザのフォロー有無を確認します。
     *
     * @param followerId フォローしているユーザ
     * @param followedId フォローされているユーザ
     * @return フォローしている場合はtrue、フォローしていない場合はfalse
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId")
    Boolean isFollowing(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    /**
     * 指定したユーザがフォローしているユーザ数をカウントします。
     *
     * @param userId ユーザID
     * @return 指定したユーザがフォローしているユーザ数
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    Long countFollowedUsers(@Param("userId") Long userId);

    /**
     * 指定したユーザをフォローしているユーザ数をカウントします。
     *
     * @param userId ユーザID
     * @return 指定したユーザをフォローしているユーザ数
     */
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed.id = :userId")
    Long countFollowers(@Param("userId") Long userId);

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
