package com.example.bookstore.repository.jpa;

import com.example.bookstore.dto.repository.DashboardUserRepositoryDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ユーザアーティストリレーションインターフェース
 */
public interface UserArtistRepository extends JpaRepository<UserArtist, UserArtistId> {


    /**
     * 指定されたユーザの好きなアーティスト一覧を取得します。
     *
     * @param userId ユーザID
     * @return アーティスト一覧
     */
    @Query("SELECT ua.artist FROM UserArtist ua WHERE ua.user.id = :userId")
    List<Artist> findFavoriteArtistsByUserId(@Param("userId") Long userId);

    /**
     * 指定されたアーティストを好きなユーザ一覧を取得します。
     *
     * @param artistId アーティストID
     * @return ユーザ一覧
     */
    @Query("SELECT ua.user FROM UserArtist ua WHERE ua.artist.id = :artistId")
    List<User> findUsersByFavoriteArtistId(@Param("artistId") String artistId);


    /**
     * 共通のアーティストを好きなユーザの中から、フォロー状況を含めて取得します。
     * フォローしていないユーザを上位にし、最大10件を返します。
     *
     * @param artistIds アーティストIDリスト
     * @param userId    ユーザID
     * @return ユーザのリストとそのフォロー状況
     */
    @Query("SELECT new com.example.bookstore.dto.repository.DashboardUserRepositoryDto(u, " +
            "CASE WHEN (SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId AND f.followed.id = u.id) > 0 THEN true ELSE false END) " +
            "FROM User u " +
            "JOIN u.userArtists ua " +
            "WHERE ua.artist.id IN :artistIds " +
            "AND u.id <> :userId " +
            "GROUP BY u.id " +
            "ORDER BY CASE WHEN (SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId AND f.followed.id = u.id) > 0 THEN 1 ELSE 0 END ASC")
    List<DashboardUserRepositoryDto> findRecommendedUsersWithFollowStatus(
            @Param("artistIds") List<String> artistIds,
            @Param("userId") Long userId
    );

    /**
     * 指定されたユーザと好きなアーティストが共通しているユーザをアーティスト情報とともに取得します。
     * 取得結果はユーザID及びアーティストIDの昇順にソートされています。また結果には同一ユーザが重複する可能性があります。
     *
     * @param userId ユーザID
     * @return 同じアーティストが好きな他のユーザとアーティストのリスト
     */
    @Query("SELECT ua FROM UserArtist ua " +
            "WHERE ua.artist.id IN (SELECT ua2.artist.id FROM UserArtist ua2 WHERE ua2.user.id = :userId) " +
            "AND ua.user.id != :userId " +
            "ORDER BY ua.user.id ASC, ua.artist.id ASC")
    List<UserArtist> findUserSameFavorite(@Param("userId") Long userId);

    /**
     * 指定されたユーザIDとアーティストIDに基づいて、該当するリレーションが存在するかを確認します。
     *
     * @param userId   ユーザID
     * @param artistId アーティストID
     * @return 該当するリレーションが存在する場合true、それ以外の場合false
     */
    @Query("SELECT CASE WHEN COUNT(ua) > 0 THEN true ELSE false END " +
            "FROM UserArtist ua " +
            "WHERE ua.user.id = :userId AND ua.artist.id = :artistId")
    boolean existsByUserIdAndArtistId(@Param("userId") Long userId, @Param("artistId") String artistId);

    /**
     * 指定されたユーザIDに紐づくリレーション情報を全て削除します。
     *
     * @param userId ユーザID
     */
    @Modifying
    @Query("DELETE FROM UserArtist ua WHERE ua.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);


}
