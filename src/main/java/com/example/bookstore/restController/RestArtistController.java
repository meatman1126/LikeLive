package com.example.bookstore.restController;


import com.example.bookstore.dto.form.artist.ArtistUpdateForm;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Restアーティストコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestArtistController {

    /**
     * アーティストサービス
     */
    @Autowired
    ArtistService artistService;

    /**
     * 指定されたアーティスト情報を取得します。
     *
     * @param artistId アーティストID
     * @return アーティスト情報
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<Artist> getArtist(@PathVariable String artistId) {
        Artist artist = artistService.findById(artistId);
        return ResponseEntity.ok(artist);
    }

    /**
     * アーティスト情報を更新します。
     *
     * @param form     アーティストの更新情報
     * @param artistId 更新対象のアーティストID
     * @return アーティストの更新結果
     */
    @PostMapping("/artist/update/{artistId}")
    public ResponseEntity<Artist> updateArtist(@ModelAttribute ArtistUpdateForm form,
                                               @PathVariable String artistId
    ) {
        Artist artist = Artist.builder()
                .name(form.getName())
                .imageUrl(form.getImageUrl())
                .build();

        Artist updatedArtist = artistService.updateArtist(artistId, artist);
        return ResponseEntity.ok(updatedArtist);
    }

}

