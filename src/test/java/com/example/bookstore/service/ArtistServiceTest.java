package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.jpa.ArtistRepository;

public class ArtistServiceTest extends SpringBootTestBase {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    private User testUser;
    private Artist testArtist;

    @BeforeEach
    void setUp() {
        // テストユーザーを作成し、認証情報を設定
        testUser = createUserAndSetupAuthentication(testDataSetup);

        // テストアーティストを作成
        testArtist = testDataSetup.createTestArtist();
    }

    @Test @DisplayName("IDでアーティストを検索できる")
    void findById_ExistingArtist_ReturnsArtist() {
        // 実行
        Artist foundArtist = artistService.findById(testArtist.getId());

        // 検証
        assertNotNull(foundArtist);
        assertEquals(testArtist.getId(), foundArtist.getId());
        assertEquals(testArtist.getName(), foundArtist.getName());
    }

    @Test @DisplayName("存在しないIDでアーティストを検索するとnullが返る")
    void findById_NonExistingArtist_ReturnsNull() {
        // 存在しないID
        String nonExistingId = "non-existing-" + UUID.randomUUID().toString();

        // 実行
        Artist foundArtist = artistService.findById(nonExistingId);

        // 検証
        assertNull(foundArtist);
    }

    @Test @DisplayName("新規アーティストを保存できる")
    void saveArtist_NewArtist_SavesArtist() {
        // 新規アーティスト
        String newArtistId = "new-artist-" + UUID.randomUUID().toString();
        Artist newArtist = Artist.builder().id(newArtistId).name("New Test Artist").build();

        // 実行
        Artist savedArtist = artistService.saveArtist(newArtist);

        // 検証
        assertNotNull(savedArtist);
        assertEquals(newArtistId, savedArtist.getId());
        assertEquals("New Test Artist", savedArtist.getName());
        assertEquals(testUser.getId().toString(), savedArtist.getCreatedBy());
        assertEquals(testUser.getId().toString(), savedArtist.getUpdatedBy());

        // DBから取得して再確認
        Artist dbArtist = artistRepository.findById(newArtistId).orElse(null);
        assertNotNull(dbArtist);
        assertEquals(newArtistId, dbArtist.getId());
    }

    @Test @DisplayName("既存のIDでアーティストを保存しても新規登録されない")
    void saveArtist_ExistingArtistId_ReturnsExistingArtist() {
        // 既存アーティストと同じIDで別の情報を持つアーティスト
        Artist duplicateArtist = Artist.builder().id(testArtist.getId()).name("Updated Artist Name").build();

        // 実行
        Artist savedArtist = artistService.saveArtist(duplicateArtist);

        // 検証 - 既存のアーティストが返却される（更新されていない）
        assertNotNull(savedArtist);
        assertEquals(testArtist.getId(), savedArtist.getId());
        assertEquals(testArtist.getName(), savedArtist.getName()); // 名前が更新されていないことを確認
    }

    @Test @DisplayName("アーティスト情報を更新できる")
    void updateArtist_ExistingArtist_UpdatesArtist() {
        // 更新用データ
        Artist updateData = Artist.builder().id(testArtist.getId()).name("Updated Artist Name")
                .imageUrl("https://example.com/updated-image.jpg").build();

        // 実行
        Artist updatedArtist = artistService.updateArtist(testArtist.getId(), updateData);

        // 検証
        assertNotNull(updatedArtist);
        assertEquals(testArtist.getId(), updatedArtist.getId());
        assertEquals("Updated Artist Name", updatedArtist.getName());
        assertEquals("https://example.com/updated-image.jpg", updatedArtist.getImageUrl());
        assertEquals(testUser.getId().toString(), updatedArtist.getUpdatedBy());

        // DBから取得して再確認
        Artist dbArtist = artistRepository.findById(testArtist.getId()).orElse(null);
        assertNotNull(dbArtist);
        assertEquals("Updated Artist Name", dbArtist.getName());
        assertEquals("https://example.com/updated-image.jpg", dbArtist.getImageUrl());
    }

    @Test @DisplayName("存在しないIDでアーティストを更新すると例外がスローされる")
    void updateArtist_NonExistingArtist_ThrowsException() {
        // 存在しないID
        String nonExistingId = "non-existing-" + UUID.randomUUID().toString();

        // 更新用データ
        Artist updateData = Artist.builder().id(nonExistingId).name("Updated Artist Name").build();

        // 実行と検証
        assertThrows(IllegalStateException.class, () -> {
            artistService.updateArtist(nonExistingId, updateData);
        });
    }
}