package com.example.bookstore.service;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.repository.jpa.ArtistRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ArtistService {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    UserUtilService userUtilService;

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
     * 既にDBに登録済みのアーティストの場合はinsertは行いません。
     *
     * @param input artist情報
     * @return 登録されたartist情報
     */
    public Artist saveArtist(Artist input) {
        Artist artist = artistRepository.findById(input.getId()).orElse(null);
        String currentUserId = userUtilService.getCurrentUser().getId().toString();
        input.setCreatedBy(currentUserId);
        input.setUpdatedBy(currentUserId);

        //inputされたartistが未登録の場合DBへ登録する
        return Objects.requireNonNullElseGet(artist, () -> artistRepository.save(input));
    }


}
