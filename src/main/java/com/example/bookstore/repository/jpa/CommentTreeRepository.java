package com.example.bookstore.repository.jpa;

import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.key.CommentTreeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentTreeRepository extends JpaRepository<CommentTree, CommentTreeId> {

    /**
     * 指定された親コメントに対する返信の数を取得します。
     *
     * @param parentCommentId 親コメントのID
     * @return 返信コメントの数
     */
    @Query("SELECT COUNT(ct) FROM CommentTree ct WHERE ct.parentComment.id = :parentCommentId")
    Integer countRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);
}
