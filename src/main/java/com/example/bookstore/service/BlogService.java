package com.example.bookstore.service;

import com.example.bookstore.dto.repository.DashboardRepositoryDto;
import com.example.bookstore.dto.view.BlogInfoViewDto;
import com.example.bookstore.dto.view.DashboardViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.BlogStatus;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ブログサービス
 */
@Service
public class BlogService {

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * ブログリポジトリ
     */
    @Autowired
    private BlogRepository blogRepository;

    /**
     * 通知サービス
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * フォローサービス
     */
    @Autowired
    private FollowService followService;

    /**
     * ブログアーティストサービス
     */
    @Autowired
    private BlogArtistService blogArtistService;

    /**
     * エンティティマネージャ
     */
    @Autowired
    private EntityManager entityManager;


    //ローカル開発の段階でElasticsearchを導入するのが難しいため一旦保留する
//    @Autowired
//    BlogSearchRepository blogSearchRepository;

//    public List<Blog> searchBlogs(String keyword) {
//        return blogSearchRepository.findByTitleContainingOrContentContainingOrTagsContaining(keyword);
//    }

    /**
     * キーワードに合致するブログ情報を取得します。
     *
     * @param keyword 検索キーワード
     * @return 検索結果
     */
    public List<Blog> searchBlog(String keyword) {
        return blogRepository.searchBlogsByKeyword(keyword);
    }


    /**
     * 指定されたブログ情報を取得します。
     *
     * @param blogId ブログID
     * @return ブログ情報
     */
    public Blog findById(Long blogId) {
        return blogRepository.findById(blogId).orElseThrow();
    }

    /**
     * 指定されたブログ情報を取得します。
     *
     * @param blogId ブログID
     * @return ブログ情報
     */
    public BlogInfoViewDto findBlogInfo(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow();
        List<Artist> artistList = blogArtistService.findArtistsByBlogId(blogId);

        return BlogInfoViewDto.builder()
                .blog(blog)
                .artistList(artistList)
                .build();
    }

    public List<DashboardViewDto> findInterestBlogs(Long userId) {
        List<DashboardRepositoryDto> repositoryDtoList = blogRepository.findInterestBlogs(userId, PageRequest.of(0, 10));
        return DashboardViewDto.toViewDto(repositoryDtoList);
    }

    /**
     * ブログ情報を登録します。
     *
     * @param input 登録するブログ情報
     * @return 登録されたブログ情報
     */
    @Transactional
    public Blog createBlog(Blog input, List<String> artistIdList) {
        // DBへの登録
        // ブログデータの登録
        Blog createdBlog = blogRepository.save(input);
        // ブログアーティスト関連データの登録
        blogArtistService.saveBlogArtist(input, artistIdList);


        //Elasticsearchのインデックスを登録
//        blogSearchRepository.save(createdBlog);

        // ブログの作成通知を作成(下書きの場合は通知は作成しない)
        if (input.getStatus().equals(BlogStatus.PUBLISHED)) {
            saveNotificationOfBlogCreated(createdBlog);
        }
        return createdBlog;
    }


    /**
     * ブログ情報を更新します。
     *
     * @param input 更新情報
     */
    @Transactional
    public Blog updatedBlog(Blog input, List<String> artistIdList) {
        boolean isCreateNotification = false;
        Blog beforeUpdateBlog = blogRepository.findById(input.getId()).orElseThrow();
        if (beforeUpdateBlog.getStatus().equals(BlogStatus.DRAFT) && input.getStatus().equals(BlogStatus.PUBLISHED)) {
            isCreateNotification = true;
        }

        //DBを更新
        blogRepository.update(input);

        // 関連アーティスト情報を保存
        blogArtistService.saveBlogArtist(input, artistIdList);

        Blog updatedBlog = blogRepository.findById(input.getId()).orElseThrow();


        //Elasticsearchのインデックスを更新
//        blogSearchRepository.save(updatedBlog);

        entityManager.flush();
        entityManager.refresh(updatedBlog);

        if (isCreateNotification) {
            saveNotificationOfBlogCreated(updatedBlog);
        }

        return updatedBlog;
    }

    /**
     * ブログのいいね回数を更新します。
     *
     * @param blogId   更新対象のブログID
     * @param isCansel いいね取り消しフラグ
     * @return 更新後のいいね数
     */
    public int updatedLikeCount(Long blogId, Boolean isCansel) {
        Blog blog = blogRepository.findById(blogId).orElseThrow();
        int updatedLikeCount;
        if (isCansel) {
            updatedLikeCount = blog.getLikeCount() - 1;
        } else {
            updatedLikeCount = blog.getLikeCount() + 1;
        }
        blogRepository.updateLikeCount(blogId, updatedLikeCount, userUtilService.getCurrentUserId());
        return updatedLikeCount;
    }

    /**
     * ブログの閲覧回数を更新します。
     *
     * @param blogId 更新対象のブログID
     */
    public int updatedViewCount(Long blogId) {
        int updatedViewCount = blogRepository.findById(blogId).orElseThrow().getViewCount();
        blogRepository.updateViewCount(blogId, updatedViewCount, userUtilService.getCurrentUserId());
        return updatedViewCount;
    }

    /**
     * ブログのコメント数を更新します。
     *
     * @param blogId   更新対象のブログID
     * @param isCansel コメント取り消しフラグ
     * @return 更新後のコメント数
     */
    public int updatedCommentCount(Long blogId, Boolean isCansel) {
        Blog blog = blogRepository.findById(blogId).orElseThrow();
        int updatedCommentCount;
        if (isCansel) {
            updatedCommentCount = blog.getCommentCount() - 1;
        } else {
            updatedCommentCount = blog.getCommentCount() + 1;
        }
        blogRepository.updateCommentCount(blogId, updatedCommentCount, userUtilService.getCurrentUserId());
        return updatedCommentCount;
    }

    /**
     * ブログ情報を削除します。
     *
     * @param blogId 削除対象のブログID
     */
    @Transactional
    public void deleteBlog(Long blogId) {
        // DBのデータを削除
        blogRepository.delete(blogId, true, userUtilService.getCurrentUser().getId().toString());
        // Elasticsearchのインデックス削除
//        blogSearchRepository.deleteById(blogId);
        // 関連する未読通知の削除
        deleteNotificationOfBlogCreated(blogId);
    }

    /**
     * 指定されたブログ作成の通知を作成します。
     *
     * @param blog 通知の対象となるブログ
     */
    private void saveNotificationOfBlogCreated(Blog blog) {
        User triggerUser = userUtilService.getCurrentUser();

        // ログインユーザのフォロワーを取得する
        List<User> followedList = followService.getFollowers();
        // 各フォロワーに対して通知データを作成する。
        for (User user : followedList) {
            Notification notification = Notification.builder()
                    .targetUser(user)
                    .notificationType(NotificationType.BLOG_CREATED)
                    .relatedBlog(blog)
                    .notificationCreatedAt(LocalDateTime.now())
                    .isRead(false)
                    .triggerUser(triggerUser)
                    .isDeleted(false)
                    .createdBy(triggerUser.getId().toString())
                    .updatedBy(triggerUser.getId().toString())
                    .build();

            notificationService.saveNotification(notification);
        }
    }

    /**
     * 指定されたブログの未読通知を削除します。
     *
     * @param blogId ブログID
     */
    private void deleteNotificationOfBlogCreated(Long blogId) {
        //削除されたブログに関する通知を検索し未読の場合削除する
        List<Notification> notifications = notificationService.getUnreadBlogCreatedNotificationsByBlogId(blogId);
        if (!notifications.isEmpty()) {
            notificationService.deleteNotifications(
                    notifications.stream().map(Notification::getId).collect(Collectors.toList()));
        }
    }


}
