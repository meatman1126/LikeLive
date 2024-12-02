package com.example.bookstore.repository.jpa;

import com.example.bookstore.dto.repository.ProfileRepositoryDto;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ユーザリポジトリインターフェース
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 指定されたsubjectに紐づくユーザ情報を取得します。
     *
     * @param subject ユーザ一意の文字列（Idpにより提供）
     * @return ユーザ情報
     */
    Optional<User> findBySubject(String subject);

    /**
     * 指定されたユーザ情報を更新します。
     *
     * @param id          ユーザID
     * @param displayName ユーザ名
     * @param filePath    プロフィール画像パス
     * @param updatedAt   更新日時
     */
    @Modifying
    @Query("UPDATE User u SET u.displayName = :displayName, " +
            "u.profileImageUrl = COALESCE(:filePath, u.profileImageUrl), " +
            "u.updatedBy = CONCAT('', :id), " +
            "u.updatedAt = :updatedAt " +
            "WHERE u.id = :id")
    void updateProfile(@Param("id") Long id,
                       @Param("displayName") String displayName,
                       @Param("filePath") String filePath,
                       @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 指定されたユーザ情報を削除します。
     *
     * @param id ユーザID
     */
    @Modifying
    @Query("UPDATE User u SET u.enabled = false, u.updatedBy = CONCAT('', :id) WHERE u.id = :id")
    void deleteUser(@Param("id") Long id);

    /**
     * ユーザ情報を更新します。
     *
     * @param id               ユーザID
     * @param displayName      表示名
     * @param selfIntroduction 自己紹介文
     * @param profileImageUrl  プロフィール画像URL
     */
    @Modifying
    @Query("UPDATE User u SET u.displayName = :displayName, u.selfIntroduction = :selfIntroduction, " +
            "u.profileImageUrl = :profileImageUrl, u.updatedBy = CONCAT('', :id) WHERE u.id = :id")
    void updateUserProfile(
            @Param("id") Long id,
            @Param("displayName") String displayName,
            @Param("selfIntroduction") String selfIntroduction,
            @Param("profileImageUrl") String profileImageUrl
    );

    /**
     * 指定されたユーザ情報を取得します。
     * 好きなアーティスト情報も合わせて取得します。
     *
     * @param userId ユーザID
     * @return ユーザ情報（好きなアーティスト情報）
     */
    @EntityGraph(attributePaths = {"userArtists.artist"})
    @Query("SELECT new com.example.bookstore.dto.repository.ProfileRepositoryDto(u.id, u.displayName, u.profileImageUrl, u.selfIntroduction, " +
            " (SELECT DISTINCT a FROM UserArtist ua JOIN ua.artist a WHERE ua.user.id = :userId), " +
            " (SELECT DISTINCT b FROM Blog b WHERE b.author.id = :userId AND b.isDeleted = false)) " +
            "FROM User u WHERE u.id = :userId")
    ProfileRepositoryDto findUserProfileWithBlogsAndArtists(@Param("userId") Long userId);

//    @Query("SELECT DISTINCT u FROM User u " +
//            "LEFT JOIN u.userArtists ua " +
//            "LEFT JOIN ua.artist a " +
//            "WHERE u.displayName LIKE %:keyword% " +
//            "OR a.name LIKE %:keyword%")
//    List<User> searchUser(@Param("keyword") String keyword, @Param("currentUserId") String currentUserId);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.userArtists ua " +
            "LEFT JOIN ua.artist a " +
            "WHERE (u.displayName LIKE %:keyword% OR a.name LIKE %:keyword%) " +
            "AND u.id != :currentUserId")
    List<User> searchUser(@Param("keyword") String keyword, @Param("currentUserId") String currentUserId);
}
