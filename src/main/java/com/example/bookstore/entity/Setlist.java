package com.example.bookstore.entity;

import lombok.Data;

import java.util.List;

/**
 * セットリストエンティティ
 */
@Data
public class Setlist {
    /**
     * 本編
     */
    private List<Track> mainSetList;


    /**
     * アンコールセクション
     */
    private List<List<Track>> encoreSections;

    /**
     * トラックエンティティ
     */
    @Data
    public static class Track {
        /**
         * トラックナンバー（何曲目か）
         */
        private int trackNumber;

        /**
         * 曲名
         */
        private String trackName;

    }
}
