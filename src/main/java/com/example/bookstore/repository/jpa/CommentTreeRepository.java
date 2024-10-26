package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.key.CommentTreeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentTreeRepository extends JpaRepository<CommentTree, CommentTreeId> {

    /**
     * 指定された親コメントに対する返信の数を取得します。
     *
     * @param parentCommentId 親コメントのID
     * @return 返信コメントの数
     */
    @Query("SELECT COUNT(ct) FROM CommentTree ct WHERE ct.parentComment.id = :parentCommentId")
    Integer countRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);
    
    /**
     * 指定された親コメントに関連する全ての子コメントを取得する
     *
     * @param parentCommentId 親コメントID
     * @return 返信コメント
     */
    @Query("SELECT ct.replyComment " +
            "FROM CommentTree ct " +
            "JOIN FETCH ct.replyComment.author " + // 即時ロードするためにJOIN FETCHを使用
            "WHERE ct.parentComment.id = :parentCommentId " +
            "ORDER BY ct.replyNumber ASC")
    List<Comment> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);


}
