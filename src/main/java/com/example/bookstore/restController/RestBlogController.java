package com.example.bookstore.restController;

import com.example.bookstore.dto.form.blog.BlogCountUpdateForm;
import com.example.bookstore.dto.form.blog.BlogDeleteForm;
import com.example.bookstore.dto.form.blog.BlogRegistrationForm;
import com.example.bookstore.dto.form.blog.BlogUpdateForm;
import com.example.bookstore.dto.view.BlogInfoViewDto;
import com.example.bookstore.dto.view.DashboardViewDto;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.BlogService;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 検索キーワードに合致するブログ情報を取得します。
     *
     * @param keyword 検索キーワード
     * @return 検索結果
     */
    @GetMapping("/blog/search")
    public ResponseEntity<Map<String, Object>> searchBlogs(@RequestParam("keyword") String keyword) {
        List<Blog> blogs = blogService.searchBlog(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("resultCount", blogs.size());

        if (blogs.isEmpty()) {
            response.put("message", "検索結果がありません");
        } else {
            response.put("blogs", blogs);  // 検索結果があれば結果を含める
        }

        return ResponseEntity.ok(response);
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
     * 自分が作成したブログを除外し、フォローしているユーザーのブログを優先的に取得する
     * <p>
     * * @return ブログのリスト
     */
    @GetMapping("/blog/interest")
    public ResponseEntity<List<DashboardViewDto>> getBlogsOfInterest() {
        // サービスメソッドを呼び出して、ブログデータを取得
        List<DashboardViewDto> viewDtoList = blogService.findInterestBlogs(userUtilService.getCurrentUser().getId());

        // ブログリストをレスポンスとして返す
        return ResponseEntity.ok(viewDtoList);
    }


    /**
     * ブログ情報を新規登録します。
     *
     * @param form 登録するブログ情報
     * @return ブログ登録処理の結果
     */
    @PostMapping("/blog/create")
    public ResponseEntity<Blog> createBlog(@RequestBody BlogRegistrationForm form) {
        LocalDateTime now = LocalDateTime.now();
        User currentUser = userUtilService.getCurrentUser();
        String currentUserId = currentUser.getId().toString();
        Blog blog = Blog.builder()
                .title(form.getTitle())
                .content(form.getContent())
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
                .setlist(form.getSetlist())
                .isDeleted(false)
                .createdBy(currentUserId)
                .updatedBy(currentUserId)
                .build();

        Blog createdBlog = blogService.createBlog(blog, form.getArtistIdList());
        return ResponseEntity.ok(createdBlog);
    }

    /**
     * ブログ情報を更新します。
     *
     * @param form ブログの更新情報
     * @return ブログの更新結果
     */
    @PostMapping("/blog/update")
    public ResponseEntity<Blog> updateBlog(@RequestBody BlogUpdateForm form) {
        LocalDateTime now = LocalDateTime.now();
        User currentUser = userUtilService.getCurrentUser();
        String currentUserId = currentUser.getId().toString();
        Blog blog = Blog.builder()
                .id(form.getId())
                .title(form.getTitle())
                .content(form.getContent())
                .blogUpdatedTime(now)
                .status(form.getStatus())
                .tags(form.getTags())
                .thumbnailUrl(form.getThumbnailUrl())
                .slug(form.getSlug())
                .category(form.getCategory())
                .setlist(form.getSetlist())
                .updatedBy(currentUserId)
                .updatedAt(now)
                .build();

        Blog updatedBlog = blogService.updatedBlog(blog, form.getArtistIdList());
        return ResponseEntity.ok(updatedBlog);

    }

    /**
     * ブログ情報を削除します。
     *
     * @param form 削除対象のブログ情報
     * @return ブログの削除処理の結果
     */
    @PostMapping("/blog/delete")
    public ResponseEntity<Blog> deleteBlog(@RequestBody BlogDeleteForm form) {
        blogService.deleteBlog(form.getId());
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
     * ブログのいいね件数を更新します。
     *
     * @param form いいね（いいね取り消し）されたブログID
     * @return 更新後のいいね件数
     */
    @PostMapping("/blog/like")
    public ResponseEntity<Integer> updateLikeCount(@RequestBody BlogCountUpdateForm form) {
        Integer updatedLikeCount = blogService.updatedLikeCount(form.getId(), form.getIsCancel());
        return ResponseEntity.ok(updatedLikeCount);

    }
}
