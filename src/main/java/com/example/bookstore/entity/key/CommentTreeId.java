package com.example.bookstore.entity.key;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommentTreeId implements Serializable {

    private Long parentCommentId;
    private Long replyCommentId;

    // デフォルトコンストラクタ
    public CommentTreeId() {
    }

    // コンストラクタ
    public CommentTreeId(Long parentCommentId, Long replyCommentId) {
        this.parentCommentId = parentCommentId;
        this.replyCommentId = replyCommentId;
    }

    // ゲッターとセッター
    public Long getparentCommentId() {
        return parentCommentId;
    }

    public void setparentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Long getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(Long replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    // equalsとhashCodeメソッドのオーバーライド
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentTreeId that = (CommentTreeId) o;
        return Objects.equals(parentCommentId, that.parentCommentId) &&
                Objects.equals(replyCommentId, that.replyCommentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentCommentId, replyCommentId);
    }
}
