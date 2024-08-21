package com.example.bookstore.service;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    @Autowired
    ArtistRepository artistRepository;

    /**
     * idに合致するartist情報を取得します。
     *
     * @param id artistを一意に識別する文字列
     * @return idに合致するartist
     */
    public Artist findById(String id) {

        return artistRepository.findById(id).orElse(null);
    }

    /**
     * artistを新規にDBに登録します。
     *
     * @param artist artist情報
     * @return 登録されたartist情報
     */
    public Artist saveArtist(Artist artist) {
        return artistRepository.save(artist);
    }


}
