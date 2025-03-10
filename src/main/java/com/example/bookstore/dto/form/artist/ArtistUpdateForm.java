package com.example.bookstore.dto.form.artist;

import lombok.Builder;
import lombok.Data;

/**
 * アーティスト情報更新Form
 */
@Data
@Builder
public class ArtistUpdateForm {

    /**
     * アーティスト名
     */
    private String name;

    /**
     * アーティスト画像URL
     */
    private String imageUrl;

}
