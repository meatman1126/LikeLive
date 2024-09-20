package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
