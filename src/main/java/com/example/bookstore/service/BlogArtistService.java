package com.example.bookstore.service;


import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.BlogArtist;
import com.example.bookstore.repository.jpa.BlogArtistRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ブログアーティストサービス
 */
@Service
public class BlogArtistService {

    /**
     * ブログアーティストリポジトリ
     */
    @Autowired
    BlogArtistRepository blogArtistRepository;

    /**
     * アーティストサービス
     */
    @Autowired
    private ArtistService artistService;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * ブログエンティティとアーティストIDリストをパラメータに受け取り、
     * BlogArtistエンティティに登録します。
     * 重複を避けるために、事前に該当するブログIDのBlogArtistデータはすべて削除します。
     *
     * @param blog         ブログエンティティ
     * @param artistIdList アーティストIDリスト
     * @return 登録されたBlogArtistのリスト
     */
    @Transactional
    public List<BlogArtist> saveBlogArtist(Blog blog, List<String> artistIdList) {
        // 該当するブログの既存のBlogArtistデータを削除
        blogArtistRepository.deleteByBlogId(blog.getId());

        List<BlogArtist> savedBlogArtists = new ArrayList<>();
        // アーティストIDリストが空の場合は登録処理をスキップ
        if (artistIdList == null || artistIdList.isEmpty()) {
            return savedBlogArtists;
        }

        // アーティストIDリストをループしてアーティストを取得し、BlogArtistを保存
        for (String artistId : artistIdList) {
            // アーティストをIDで取得
            Artist artist = artistService.findById(artistId);

            // アーティストが存在する場合のみBlogArtistエンティティを作成
            if (artist != null) {
                BlogArtist blogArtist = BlogArtist.builder()
                        .artist(artist)
                        .blog(blog)
                        .createdBy(userUtilService.getCurrentUserId())
                        .updatedBy(userUtilService.getCurrentUserId())
                        .build();

                // BlogArtistを保存
                blogArtistRepository.save(blogArtist);
                savedBlogArtists.add(blogArtist);
            }
        }

        // 新たに登録されたBlogArtistのリストを返す
        return savedBlogArtists;
    }

    /**
     * 指定されたブログIDに関連するBlogArtistエンティティを取得します。
     *
     * @param blogId ブログID
     * @return 指定されたブログに関連するBlogArtistのリスト
     */
    public List<BlogArtist> getBlogArtistsByBlogId(Long blogId) {
        return blogArtistRepository.findByBlogId(blogId);
    }


    public List<Artist> findArtistsByBlogId(Long blogId) {
        return blogArtistRepository.findArtistsByBlogId(blogId);
    }

    /**
     * 指定されたアーティストIDに関連するBlogArtistエンティティを取得します。
     *
     * @param artistId アーティストID
     * @return 指定されたアーティストに関連するBlogArtistのリスト
     */
    public List<BlogArtist> getBlogArtistsByArtistId(String artistId) {
        return blogArtistRepository.findByArtistId(artistId);
    }
}
