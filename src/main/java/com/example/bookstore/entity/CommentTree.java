package com.example.bookstore.entity;

import com.example.bookstore.entity.key.CommentTreeId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@NoArgsConstructor
@Data
public class CommentTree extends BaseEntity {

    // 複合主キー
    @EmbeddedId
    private CommentTreeId id;

    // コメントの祖先を参照
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentCommentId")
    @JoinColumn(name = "parent_comment_id", nullable = false)
    private Comment parentComment;

    // 返信されたコメントを参照
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("replyCommentId")
    @JoinColumn(name = "reply_comment_id", nullable = false)
    private Comment replyComment;

    // 返信No（祖先コメントの何件目の返信かを保持）
    @Column(name = "reply_number", nullable = false)
    private int replyNumber;

}
