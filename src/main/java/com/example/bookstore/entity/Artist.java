package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "artists")
@SuperBuilder
@Data
@NoArgsConstructor
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
