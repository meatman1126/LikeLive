package com.example.bookstore.repository;

import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
//    User findByEmail(String email);

    User findBySubject(String subject);

    @Modifying
    @Query("UPDATE User u SET u.displayName = :displayName WHERE u.id = :id")
    void updateDisplayName(@Param("id") Long id, @Param("displayName") String displayName);
}
