package com.example.bookstore.dto.view;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ブログ情報のビューDTO
 */
@Data
@Builder
public class BlogInfoViewDto {
    /**
     * ブログエンティティ
     */
    private Blog blog;

    /**
     * アーティストリスト
     */
    private List<Artist> artistList;
}
