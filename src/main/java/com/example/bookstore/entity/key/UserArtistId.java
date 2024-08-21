package com.example.bookstore.entity.key;

import java.io.Serializable;
import java.util.Objects;

public class UserArtistId implements Serializable {

    private Long userId;
    private String artistId;

    // Default constructor
    public UserArtistId() {
    }

    // Parameterized constructor
    public UserArtistId(Long userId, String artistId) {
        this.userId = userId;
        this.artistId = artistId;
    }

    // Getters, Setters, equals, and hashCode
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserArtistId that = (UserArtistId) o;
        return userId.equals(that.userId) && artistId.equals(that.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, artistId);
    }
}