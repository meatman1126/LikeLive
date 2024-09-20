package com.example.bookstore.dto.form.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDeleteForm {
    /**
     * コメントID
     */
    private Long commentId;
}
