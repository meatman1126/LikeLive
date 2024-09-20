package com.example.bookstore.dto.form.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUpdateForm {


    /**
     * コメントID
     */
    private Long commentId;

    /**
     * コメントの内容
     */
    private String content;

}


