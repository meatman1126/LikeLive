package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.BlogArtist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BlogArtistエンティティに対するリポジトリインターフェース。
 * ブログとアーティストの多対多リレーションの管理を行う。
 */
@Repository
public interface BlogArtistRepository extends JpaRepository<BlogArtist, Long> {

    /**
     * 指定されたアーティストIDに関連するBlogArtistエンティティを取得します。
     * BlogエンティティとArtistエンティティを即時ロードします。
     *
     * @param artistId アーティストID
     * @return 指定されたアーティストに関連するBlogArtistのリスト
     */
    @EntityGraph(attributePaths = {"blog", "artist"})
    @Query("SELECT ba FROM BlogArtist ba WHERE ba.artist.id = :artistId")
    List<BlogArtist> findByArtistId(@Param("artistId") String artistId);

    /**
     * 指定されたブログIDに関連するBlogArtistエンティティを取得します。
     * BlogエンティティとArtistエンティティを即時ロードします。
     *
     * @param blogId ブログID
     * @return 指定されたブログに関連するBlogArtistのリスト
     */
    @EntityGraph(attributePaths = {"blog", "artist"})
    @Query("SELECT ba FROM BlogArtist ba WHERE ba.blog.id = :blogId")
    List<BlogArtist> findByBlogId(@Param("blogId") Long blogId);

    /**
     * 指定されたブログIDに関連するArtistエンティティを取得します。
     *
     * @param blogId ブログID
     * @return 指定されたブログに関連するArtistのリスト
     */
    @Query("SELECT ba.artist FROM BlogArtist ba WHERE ba.blog.id = :blogId")
    List<Artist> findArtistsByBlogId(@Param("blogId") Long blogId);

    /**
     * 指定されたブログIDとアーティストIDに基づいてBlogArtistエンティティを削除します。
     *
     * @param blogId   ブログID
     * @param artistId アーティストID
     */
    @Modifying
    @Query("DELETE FROM BlogArtist ba WHERE ba.blog.id = :blogId AND ba.artist.id = :artistId")
    void deleteByBlogIdAndArtistId(@Param("blogId") Long blogId, @Param("artistId") String artistId);

    /**
     * 指定されたブログIDに紐づくデータを削除します。
     *
     * @param blogId ブログID
     */
    @Modifying
    @Query("DELETE FROM BlogArtist ba WHERE ba.blog.id = :blogId")
    void deleteByBlogId(@Param("blogId") Long blogId);
}