package com.example.bookstore.dto.form.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// todo 削除対象のコメントはformで受け取らず、パス変数で受け取る
public class CommentDeleteForm {
    /**
     * コメントID
     */
    private Long commentId;
}
