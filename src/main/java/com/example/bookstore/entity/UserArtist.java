package com.example.bookstore.entity;

import com.example.bookstore.entity.key.UserArtistId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true) @Entity @SuperBuilder @NoArgsConstructor @Getter
public class UserArtist extends BaseEntity {

    @EmbeddedId
    private UserArtistId id;

    @ManyToOne @MapsId("userId") @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @MapsId("artistId") @JoinColumn(name = "artist_id")
    private Artist artist;

}
