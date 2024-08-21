package com.example.bookstore.entity;

import com.example.bookstore.entity.key.UserArtistId;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserArtist extends BaseEntity {

    @EmbeddedId
    private UserArtistId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(nullable = false)
    private boolean favorite;

}
