package com.example.bookstore.dto.repository;

import com.example.bookstore.entity.Comment;
import lombok.Builder;
import lombok.Data;

/**
 * 親コメント情報を保持するRepositoryDto
 */
@Data
@Builder
public class ParentCommentRepositoryDto {

    /**
     * 親コメント情報
     */
    private Comment comment;

    /**
     * 返信件数
     */
    private Long replyCount;
}