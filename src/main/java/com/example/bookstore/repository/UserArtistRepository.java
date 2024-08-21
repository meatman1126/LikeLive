package com.example.bookstore.repository;

import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserArtistRepository extends JpaRepository<UserArtist, UserArtistId> {
}
