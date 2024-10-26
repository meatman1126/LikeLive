package com.example.bookstore.dto.view;

import com.example.bookstore.dto.repository.ParentCommentRepositoryDto;
import com.example.bookstore.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ParentCommentViewDto {
    /**
     * 親コメント情報
     */
    private Comment comment;

    /**
     * 返信件数
     */
    private Long replyCount;

    public static List<ParentCommentViewDto> build(List<ParentCommentRepositoryDto> repositoryDtoList) {
        List<ParentCommentViewDto> viewDtoList = new ArrayList<>();
        for (ParentCommentRepositoryDto repositoryDto : repositoryDtoList) {
            ParentCommentViewDto viewDto = ParentCommentViewDto.builder()
                    .comment(repositoryDto.getComment())
                    .replyCount(repositoryDto.getReplyCount())
                    .build();
            viewDtoList.add(viewDto);
        }
        return viewDtoList;
    }

}
