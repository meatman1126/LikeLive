package com.example.bookstore.dto.form.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRegistrationForm {

    /**
     * コメントの内容
     */
    private String content;

    /**
     * コメント対象のブログID
     */
    private Long blogId;

    /**
     * 親コメントID（既存のコメントに対する返信の場合使用する）
     */
    private Long parentCommentId;


}
