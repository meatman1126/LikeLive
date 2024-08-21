package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * ユーザエンティティクラス
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 表示名
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * 利用可否 利用可能ユーザの場合true
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * Idpにより提供される一意なキー
     */
    @Column
    private String subject;

    @OneToMany(mappedBy = "user")
    private Set<UserArtist> userArtists;

//    @ManyToMany
//    @JoinTable(
//            name = "user_artist",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "artist_id")
//    )
//    private Set<Artist> artists = new HashSet<>();


}
