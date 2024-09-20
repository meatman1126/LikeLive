package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {


    /**
     * キーワードに合致するブログ記事を閲覧回数の降順にソートして取得します（論理削除されていないデータ）。
     *
     * @param keyword 検索キーワード
     * @return キーワードに合致するブログ記事リスト
     */
    @Query("SELECT b FROM Blog b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND b.isDeleted = false " +
            "ORDER BY b.viewCount DESC")
    List<Blog> searchBlogsByKeyword(@Param("keyword") String keyword);

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
            "b.commentCount = :#{#blog.commentCount}, " +
            "b.content = :#{#blog.content}, " +
            "b.likeCount = :#{#blog.likeCount}, " +
            "b.slug = :#{#blog.slug}, " +
            "b.status = :#{#blog.status}, " +
            "b.tags = :#{#blog.tags}, " +
            "b.thumbnailUrl = :#{#blog.thumbnailUrl}, " +
            "b.title = :#{#blog.title}, " +
            "b.viewCount = :#{#blog.viewCount}, " +
            "b.setlist = :#{#blog.setlist} " +
            "WHERE b.id = :#{#blog.id}")
    void update(@Param("blog") Blog blog);

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
    void delete(@Param("id") Long id, @Param("isDeleted") boolean deleteFlag, @Param("updatedBy") String userId);


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
            "b.likeCount = :likeCount," +
            "b.updatedBy = :updatedBy " +
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


}
