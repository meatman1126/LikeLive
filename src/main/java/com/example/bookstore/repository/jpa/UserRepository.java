package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySubject(String subject);

    @Modifying
    @Query("UPDATE User u SET u.displayName = :displayName, u.updatedBy = CONCAT('', :id) WHERE u.id = :id")
    void updateDisplayName(@Param("id") Long id, @Param("displayName") String displayName);


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
}
