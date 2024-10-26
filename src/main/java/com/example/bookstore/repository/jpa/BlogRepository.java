package com.example.bookstore.repository.jpa;

import com.example.bookstore.dto.repository.DashboardBlogRepositoryDto;
import com.example.bookstore.entity.Blog;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ブログリポジトリインターフェース
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * 指定されたブログ情報を取得します。
     * 関連エンティティの著者情報（author）を即時ロードします。
     *
     * @param id ブログID
     * @return 指定されたブログ情報
     */
    @EntityGraph(attributePaths = {"author"})
    @NonNull Optional<Blog> findById(@NonNull Long id);

    /**
     * 指定されたユーザが作成した公開中のブログ記事を取得します。
     * 取得結果はブログ作成日時の降順にソートされます。
     *
     * @param userId ユーザID
     * @return ブログ記事リスト
     */
    @Query("SELECT b FROM Blog b " +
            "WHERE b.author.id = :userId " +
            "AND b.status = 'PUBLISHED' " +  // PUBLISHEDステータスを条件に追加
            "AND b.isDeleted = false " +
            "ORDER BY b.blogCreatedTime DESC")
    List<Blog> findPublishedBlogsByUserId(@Param("userId") Long userId);

    /**
     * 指定されたユーザが作成した下書きのブログ記事を取得します。
     * 取得結果はブログ作成日時の降順にソートされます。
     *
     * @param userId ユーザID
     * @return ブログ記事リスト
     */
    @Query("SELECT b FROM Blog b " +
            "WHERE b.author.id = :userId " +
            "AND b.status = 'DRAFT' " +  // PUBLISHEDステータスを条件に追加
            "AND b.isDeleted = false " +
            "ORDER BY b.blogCreatedTime DESC")
    List<Blog> findDraftBlogsByUserId(@Param("userId") Long userId);

    
    /**
     * キーワードに合致するブログ記事をページネーションとソートを用いて取得します。
     * ブログに関連するアーティスト名も検索対象に含め、ステータスがPUBLISHEDのもののみ取得します。
     *
     * @param keyword  検索キーワード
     * @param pageable ページネーションとソート情報
     * @return キーワードに合致するブログ記事リスト（ページネーション対応）
     */
    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN b.blogArtists ba LEFT JOIN ba.artist a WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND b.isDeleted = false AND b.status = 'PUBLISHED'")
    Page<Blog> searchBlogsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 指定したユーザがフォローしているユーザが作成したブログ記事を取得します（論理削除されていないデータ）。
     * 取得結果はブログ作成日時の降順にソートされます。
     *
     * @param userId ユーザID
     * @return ブログ記事リスト
     */
    @Query("SELECT b FROM Blog b " +
            "WHERE b.author.id IN (SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId) " +
            "AND b.isDeleted = false " +
            "ORDER BY b.blogCreatedTime DESC")
    List<Blog> findBlogsByFollowedUsers(@Param("userId") Long userId);

    /**
     * 指定したユーザがフォローしていないユーザが作成したブログ記事を取得します（論理削除されていないデータ）。
     * 取得結果はブログ作成日時の降順にソートされます。
     *
     * @param userId ユーザID
     * @return ブログ記事リスト
     */
    @Query("SELECT b FROM Blog b " +
            "WHERE b.author.id NOT IN (SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId) " +
            "AND b.isDeleted = false " +
            "ORDER BY b.blogCreatedTime DESC")
    List<Blog> findOtherBlogs(@Param("userId") Long userId);

    /**
     * blogsテーブルの指定されたレコードを更新します。
     *
     * @param blog 更新情報
     */
    @Modifying
    @Query("UPDATE Blog b SET " +
            "b.updatedBy = :#{#blog.updatedBy}, " +
            "b.blogUpdatedTime = :#{#blog.blogUpdatedTime}, " +
            "b.category = :#{#blog.category}, " +
            "b.content = :#{#blog.content}, " +
            "b.slug = :#{#blog.slug}, " +
            "b.status = :#{#blog.status}, " +
            "b.tags = :#{#blog.tags}, " +
            "b.thumbnailUrl = :#{#blog.thumbnailUrl}, " +
            "b.title = :#{#blog.title}, " +
            "b.setlist = :#{#blog.setlist} " +
            "WHERE b.id = :id")
    void update(@Param("id") Long id, @Param("blog") Blog blog);

    /**
     * blogsテーブルの指定されたレコードを論理削除します。
     *
     * @param id         削除対象のブログID
     * @param deleteFlag 削除フラグ
     * @param userId     更新ユーザID
     */
    @Modifying
    @Query("UPDATE Blog b SET " +
            "b.isDeleted = :isDeleted," +
            "b.updatedBy = :updatedBy " +
            "WHERE b.id = :id")
    void delete(@Param("id") Long id, @Param("isDeleted") Boolean deleteFlag, @Param("updatedBy") String userId);


    /**
     * blogsテーブルの指定されたレコードの閲覧回数を更新します。
     *
     * @param id        更新対象のブログID
     * @param viewCount 閲覧回数
     * @param userId    更新ユーザID
     */
    @Modifying
    @Query("UPDATE Blog b SET " +
            "b.viewCount = :viewCount," +
            "b.updatedBy = :updatedBy " +
            "WHERE b.id = :id")
    void updateViewCount(@Param("id") Long id, @Param("viewCount") int viewCount, @Param("updatedBy") String userId);

    /**
     * blogsテーブルの指定されたレコードのいいね回数を更新します。
     *
     * @param id        更新対象のブログID
     * @param likeCount 閲覧回数
     * @param userId    更新ユーザID
     */
    @Modifying
    @Query("UPDATE Blog b SET " +
            "b.likeCount = :likeCount, " +
            "b.updatedBy = :updatedBy, " +
            "b.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE b.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") int likeCount, @Param("updatedBy") String userId);

    /**
     * blogsテーブルの指定されたレコードのコメント回数を更新します。
     *
     * @param id           更新対象のブログID
     * @param commentCount 閲覧回数
     * @param userId       更新ユーザID
     */
    @Modifying
    @Query("UPDATE Blog b SET " +
            "b.commentCount = :commentCount," +
            "b.updatedBy = :updatedBy " +
            "WHERE b.id = :id")
    void updateCommentCount(@Param("id") Long id, @Param("commentCount") int commentCount, @Param("updatedBy") String userId);

    /**
     * ダッシュボードに表示する興味のあるブログ記事を取得します。
     * フォロー中のユーザの記事を優先的に取得します。
     *
     * @param userId   現在のユーザID
     * @param pageable ページネーション情報
     * @return ダッシュボード表示用のブログ記事リスト
     */
    @Query("SELECT new com.example.bookstore.dto.repository.DashboardBlogRepositoryDto(" +
            "b.id, b.title,b.thumbnailUrl, b.author.profileImageUrl, b.author.displayName, " +
            "CASE WHEN (SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId AND f.followed.id = b.author.id) > 0 THEN true ELSE false END, " +
            "b.blogCreatedTime) " +
            "FROM Blog b WHERE b.author.id <> :userId " +
            "ORDER BY CASE WHEN b.author.id IN " +
            "(SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId) THEN 0 ELSE 1 END ASC, " +
            "b.blogCreatedTime DESC")
    List<DashboardBlogRepositoryDto> findInterestBlogs(Long userId, Pageable pageable);


}
