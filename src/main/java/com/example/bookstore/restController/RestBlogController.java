package com.example.bookstore.restController;

import com.example.bookstore.dto.form.blog.BlogCountUpdateForm;
import com.example.bookstore.dto.form.blog.BlogRegistrationForm;
import com.example.bookstore.dto.form.blog.BlogUpdateForm;
import com.example.bookstore.dto.view.BlogInfoViewDto;
import com.example.bookstore.dto.view.DashboardBlogViewDto;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Setlist;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.BlogService;
import com.example.bookstore.service.util.UserUtilService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Restブログコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestBlogController {

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    UserUtilService userUtilService;

    /**
     * ブログサービス
     */
    @Autowired
    BlogService blogService;

//    /**
//     * 検索リクエストを受け取るメソッド。
//     * キーワードに基づいてブログ記事を検索し、検索結果を返します。
//     *
//     * @param keyword 検索キーワード
//     * @return 検索結果に一致するブログ記事のリスト
//     */
//    @GetMapping("/blog/search")
//    public List<Blog> searchBlogs(@RequestParam String keyword) {
//        // Elasticsearchを使用して検索結果を取得
//        return blogService.searchBlogs(keyword);
//    }

    @GetMapping("/blog/search")
    public ResponseEntity<Map<String, Object>> searchBlogs(
            @RequestParam("keyword") String keyword,
            @RequestParam("sort") String sort,
            @RequestParam("page") int page) {

        // ページネーションとソート条件を設定
        Pageable pageable = PageRequest.of(page, 10, getSort(sort));

        // 検索処理
        Page<Blog> blogs = blogService.searchBlog(keyword, pageable);

        // 次のページがあるかどうかを判定
        boolean hasMore = blogs.hasNext();

        // 結果の準備
        Map<String, Object> response = new HashMap<>();
        response.put("resultCount", blogs.getTotalElements());
        response.put("blogs", blogs.getContent());
        response.put("hasMore", hasMore); // 次のページがあるかどうかを追加

        return ResponseEntity.ok(response);
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "blogCreatedTime");
            case "mostLiked" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> Sort.by(Sort.Direction.DESC, "blogCreatedTime");
        };
    }

    /**
     * 指定されたブログ情報を取得します。
     *
     * @param blogId ブログID
     * @return ブログ情報
     */
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<BlogInfoViewDto> getBlog(@PathVariable Long blogId) {
        BlogInfoViewDto viewDto = blogService.findBlogInfo(blogId);
        return ResponseEntity.ok(viewDto);
    }

    /**
     * ログインユーザが作成した下書きリストを取得します。
     *
     * @return ブログ情報（下書き）
     */
    @GetMapping("/blog/my-drafts")
    public ResponseEntity<List<Blog>> getDrafts() {
        List<Blog> draftList = blogService.findDraftBlog(userUtilService.getCurrentUser().getId());
        return ResponseEntity.ok(draftList);
    }

    /**
     * ログインユーザが非公開ブログリストを取得します。
     *
     * @return ブログ情報（非公開）
     */
    @GetMapping("/blog/my-archives")
    public ResponseEntity<List<Blog>> getArchives() {
        List<Blog> archiveList = blogService.findArchiveBlog(userUtilService.getCurrentUser().getId());
        return ResponseEntity.ok(archiveList);
    }


    /**
     * ログインユーザが興味のありそうなブログを取得します（自身のブログは取得対象外）
     *
     * @return ブログのリスト
     */
    @GetMapping("/blog/interest")
    public ResponseEntity<List<DashboardBlogViewDto>> getBlogsOfInterest() {
        // サービスメソッドを呼び出して、ブログデータを取得
        List<DashboardBlogViewDto> viewDtoList = blogService.findInterestBlogs(userUtilService.getCurrentUser().getId());

        // ブログリストをレスポンスとして返す
        return ResponseEntity.ok(viewDtoList);
    }


    /**
     * ブログ情報を新規登録します。
     *
     * @param form           登録するブログ情報
     * @param thumbnailImage サムネイル画像ファイル
     * @return ブログ登録処理の結果
     */
    @PostMapping("/blog/create")
    public ResponseEntity<Blog> createBlog(@ModelAttribute BlogRegistrationForm form
            , @RequestParam(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
        LocalDateTime now = LocalDateTime.now();
        User currentUser = userUtilService.getCurrentUser();
        String currentUserId = currentUser.getId().toString();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // content フィールドを Map に変換
            Map<String, Object> contentMap = objectMapper.readValue(form.getContent(), new TypeReference<Map<String, Object>>() {
            });
            // setlist フィールドを Setlist オブジェクトに変換
            Setlist setlist = objectMapper.readValue(form.getSetlist(), Setlist.class);

            Blog blog = Blog.builder()
                    .title(!form.getTitle().isEmpty() ? form.getTitle() : "タイトル未設定")
                    .content(contentMap)
                    .author(currentUser)
                    .status(form.getStatus())
                    .blogCreatedTime(now)
                    .blogUpdatedTime(now)
                    .tags(form.getTags())
                    .viewCount(0)
                    .likeCount(0)
                    .commentCount(0)
                    .thumbnailUrl(form.getThumbnailUrl())
                    .slug(form.getSlug())// 修正が必要の見込み
                    .category(form.getCategory())
                    .setlist(setlist)
                    .isDeleted(false)
                    .createdBy(currentUserId)
                    .updatedBy(currentUserId)
                    .build();

            Blog createdBlog = blogService.createBlog(blog, form.getArtistIdList(), thumbnailImage);
            return ResponseEntity.ok(createdBlog);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ブログ情報を更新します。
     *
     * @param form           ブログの更新情報
     * @param blogId         更新対象のブログID
     * @param thumbnailImage サムネイル画像ファイル
     * @return ブログの更新結果
     */
    @PostMapping("/blog/update/{blogId}")
    public ResponseEntity<Blog> updateBlog(@ModelAttribute BlogUpdateForm form, @PathVariable Long blogId, @RequestParam(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {
        LocalDateTime now = LocalDateTime.now();
        User currentUser = userUtilService.getCurrentUser();
        String currentUserId = currentUser.getId().toString();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // content フィールドを Map に変換
            Map<String, Object> contentMap = objectMapper.readValue(form.getContent(), new TypeReference<Map<String, Object>>() {
            });
            // setlist フィールドを Setlist オブジェクトに変換
            Setlist setlist = objectMapper.readValue(form.getSetlist(), Setlist.class);

            Blog blog = Blog.builder()
                    .id(blogId)
                    .title(!form.getTitle().isEmpty() ? form.getTitle() : "タイトル未設定")
                    .content(contentMap)
                    .blogUpdatedTime(now)
                    .status(form.getStatus())
                    .tags(form.getTags())
                    .thumbnailUrl(form.getThumbnailUrl())
                    .slug(form.getSlug())
                    .category(form.getCategory())
                    .setlist(setlist)
                    .updatedBy(currentUserId)
                    .updatedAt(now)
                    .build();

            Blog updatedBlog = blogService.updatedBlog(blogId, blog, form.getArtistIdList(), thumbnailImage);
            return ResponseEntity.ok(updatedBlog);


        } catch (JsonProcessingException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();

        }
    }

    /**
     * ブログを非公開にします。
     *
     * @param blogId 非公開にするブログID
     * @return 成功メッセージ
     */
    @PostMapping("/blog/unpublish/{blogId}")
    public ResponseEntity<String> unpublishBlog(@PathVariable Long blogId) {
        blogService.unpublishBlog(blogId, userUtilService.getCurrentUserId());
        return ResponseEntity.ok("ブログが非公開になりました。");
    }

    /**
     * ブログ情報を削除します。
     *
     * @param blogId 削除対象のブログid
     * @return ブログの削除処理の結果
     */
    @PostMapping("/blog/delete/{blogId}")
    public ResponseEntity<Blog> deleteBlog(@PathVariable Long blogId) {
        try {
            blogService.deleteBlog(blogId);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();

    }

    /**
     * ブログの閲覧回数を更新します。
     *
     * @param form 閲覧されたブログID
     * @return 更新後の閲覧回数
     */
    @PostMapping("/blog/view")
    public ResponseEntity<Integer> updateViewCount(@RequestBody BlogCountUpdateForm form) {
        Integer updatedViewCount = blogService.updatedViewCount(form.getId());
        return ResponseEntity.ok(updatedViewCount);

    }

    /**
     * 指定したブログにいいねをします。
     *
     * @param blogId いいねをするブログID
     * @return 更新後のいいね数
     */
    @PostMapping("/blog/like/{blogId}")
    public ResponseEntity<Integer> likeBlog(@PathVariable Long blogId) {
        Integer updatedLikeCount = blogService.likeBlog(userUtilService.getCurrentUser().getId(), blogId);
        return ResponseEntity.ok(updatedLikeCount);
    }

    /**
     * 指定したブログのいいねを解除します。
     *
     * @param blogId いいねを解除するブログID
     * @return 更新後のいいね数
     */
    @PostMapping("/blog/unlike/{blogId}")
    public ResponseEntity<Integer> clearLikeBlog(@PathVariable Long blogId) {
        Integer updatedLikeCount = blogService.clearLikeBlog(userUtilService.getCurrentUser().getId(), blogId);
        return ResponseEntity.ok(updatedLikeCount);
    }
}
