package com.example.bookstore.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstore.dto.repository.ParentCommentRepositoryDto;
import com.example.bookstore.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 指定されたブログIDの親コメントを取得します。
     *
     * @param blogId ブログID
     * @return 親コメント
     */
    @EntityGraph(attributePaths = {"author"})  // author を即時ロードする
    @Query("SELECT new com.example.bookstore.dto.repository.ParentCommentRepositoryDto(c, " +
            "(SELECT COUNT(ct) FROM CommentTree ct WHERE ct.parentComment = c)) " +
            "FROM Comment c " +
            "LEFT JOIN CommentTree ct ON ct.replyComment = c " +
            "WHERE c.blog.id = :blogId " +
            "AND ct.replyComment IS NULL " +  // 結合先がないもの（親コメント）を取得
            "AND c.isDeleted = false " +
            "ORDER BY c.commentCreatedTime DESC")
    List<ParentCommentRepositoryDto> findParentCommentsByBlogId(@Param("blogId") Long blogId);

    /**
     * commentsテーブルの指定されたレコードを更新します。
     *
     * @param id          コメントID
     * @param content     コメント内容
     * @param updatedTime コメント更新日時
     * @param userId      更新ユーザID
     */
    @Modifying
    @Query("UPDATE Comment c SET " +
            "c.content = :content, " +
            "c.commentUpdatedTime = :updatedTime, " +
            "c.updatedBy = :updatedBy" +
            " WHERE c.id = :id")
    void updateCommentContent(@Param("id") Long id
            , @Param("content") String content
            , @Param("updatedTime") LocalDateTime updatedTime
            , @Param("updatedBy") String userId);


    /**
     * 指定されたIDのコメントを取得します。   
     *
     * @param commentId コメントID
     * @return コメント
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.isDeleted = false")
    Optional<Comment> findById(@Param("commentId") Long commentId);

    /**
     * commentsテーブルの指定されたレコードを論理削除します。
     *
     * @param id     コメントID
     * @param userId 更新ユーザID
     * @return 更新レコード数
     */
    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true, c.updatedBy = :updatedBy WHERE c.id = :id")
    int deleteCommentById(@Param("id") Long id, @Param("updatedBy") String userId);

}
