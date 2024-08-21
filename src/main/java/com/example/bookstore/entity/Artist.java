package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "artists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artist extends BaseEntity {

    @Id
    private String id;

    @Column
    private String name;

    @Column()
    private String imageUrl;

    @OneToMany(mappedBy = "artist")
    private Set<UserArtist> userArtists;


}
