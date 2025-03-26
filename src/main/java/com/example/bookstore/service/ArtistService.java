package com.example.bookstore.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.exception.ArtistNotFoundException;
import com.example.bookstore.repository.jpa.ArtistRepository;
import com.example.bookstore.service.util.UserUtilService;

/**
 * アーティストサービスクラス
 */
@Service
public class ArtistService {

    /**
     * アーティストリポジトリ
     */
    @Autowired
    ArtistRepository artistRepository;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    UserUtilService userUtilService;

    /**
     * idに合致するartist情報を取得します。
     *
     * @param id artistを一意に識別する文字列
     * @return idに合致するartist
     */
    public Artist findById(String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException("Artist not found with id: " + id));
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

    /**
     * 指定されたartistデータを更新します。
     *
     * @param artistId 更新対象のartistID
     * @param input    artist情報
     * @return 更新されたartist情報
     */
    public Artist updateArtist(String artistId, Artist input) {
        Artist existingArtist = artistRepository.findById(artistId).orElseThrow(
                () -> new IllegalStateException("指定されたアーティストデータは存在しません"));


        existingArtist.setName(input.getName());
        existingArtist.setImageUrl(input.getImageUrl());
        existingArtist.setUpdatedBy(userUtilService.getCurrentUser().getId().toString());
        existingArtist.setUpdatedAt(LocalDateTime.now());

        return artistRepository.save(existingArtist);
    }


}
