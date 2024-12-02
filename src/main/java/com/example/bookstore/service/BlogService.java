package com.example.bookstore.service;

import com.example.bookstore.dto.repository.DashboardBlogRepositoryDto;
import com.example.bookstore.dto.view.BlogInfoViewDto;
import com.example.bookstore.dto.view.DashboardBlogViewDto;
import com.example.bookstore.entity.*;
import com.example.bookstore.entity.code.BlogStatus;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.entity.key.UserBlogLikeId;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.repository.jpa.UserBlogLikeRepository;
import com.example.bookstore.repository.jpa.UserRepository;
import com.example.bookstore.service.util.StorageService;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
     * ユーザブログいいねリポジトリ
     */
    @Autowired
    private UserBlogLikeRepository userBlogLikeRepository;

    /**
     * ユーザリポジトリ
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storageService;

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

//    /**
//     * キーワードに合致するブログ情報を取得します。
//     *
//     * @param keyword 検索キーワード
//     * @return 検索結果
//     */
//    public List<Blog> searchBlog(String keyword) {
//        return blogRepository.searchBlogsByKeyword(keyword);
//    }

    /**
     * キーワードに合致するブログ情報をページネーションとソート付きで取得します。
     *
     * @param keyword  検索キーワード
     * @param pageable ページネーションとソート情報
     * @return 検索結果（ページネーション付き）
     */
    public Page<Blog> searchBlog(String keyword, Pageable pageable) {
        return blogRepository.searchBlogsByKeyword(keyword, pageable);
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
        Boolean isLike = userBlogLikeRepository.isLikeBlog(userUtilService.getCurrentUser().getId(), blogId);

        return BlogInfoViewDto.builder()
                .blog(blog)
                .artistList(artistList)
                .isLike(isLike)
                .build();
    }

    /**
     * 指定されたユーザが作成した下書き状態のブログ情報を取得します。
     *
     * @param userId ユーザID
     * @return ブログ情報
     */
    public List<Blog> findDraftBlog(Long userId) {
        return blogRepository.findDraftBlogsByUserId(userId);
    }

    /**
     * 指定されたユーザの非公開状態のブログ情報を取得します。
     *
     * @param userId ユーザID
     * @return ブログ情報
     */
    public List<Blog> findArchiveBlog(Long userId) {
        return blogRepository.findArchiveBlogsByUserId(userId);
    }

    public List<DashboardBlogViewDto> findInterestBlogs(Long userId) {
        List<DashboardBlogRepositoryDto> repositoryDtoList = blogRepository.findInterestBlogs(userId, BlogStatus.PUBLISHED, PageRequest.of(0, 10));
        return DashboardBlogViewDto.toViewDto(repositoryDtoList);
    }

    /**
     * ブログ情報を登録します。
     *
     * @param input          登録するブログ情報
     * @param artistIdList   アーティストIDリスト
     * @param thumbnailImage サムネイル画像ファイル
     * @return 登録されたブログ情報
     */
    @Transactional
    public Blog createBlog(Blog input, List<String> artistIdList, MultipartFile thumbnailImage) {

        // リクエストにサムネイル画像が含まれている場合保存する
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            // ファイル名を一意にするためにUUIDを使用する（ユーザIDも利用可能）
            String fileName = UUID.randomUUID().toString() + "_" + thumbnailImage.getOriginalFilename();

            // ファイルの保存処理
            String filePath = storageService.saveFile(thumbnailImage, fileName);
            // URLをブログエンティティにセット
            input.setThumbnailUrl(filePath);
        }

        // DBへの登録
        // ブログデータの登録
        Blog createdBlog = blogRepository.save(input);
        // ブログアーティスト関連データの登録
        blogArtistService.saveBlogArtist(input, artistIdList);


        // Elasticsearchのインデックスを登録
        // blogSearchRepository.save(createdBlog);

        // ブログの作成通知を作成(下書きの場合は通知は作成しない)
        if (input.getStatus().equals(BlogStatus.PUBLISHED)) {
            saveNotificationOfBlogCreated(createdBlog);
        }
        return createdBlog;
    }


    /**
     * ブログ情報を更新します。
     *
     * @param blogId         更新対象のブログID
     * @param input          ブログ更新情報
     * @param artistIdList   アーティストIDリスト
     * @param thumbnailImage サムネイル画像ファイル
     */
    @Transactional
    public Blog updatedBlog(Long blogId, Blog input, List<String> artistIdList, MultipartFile thumbnailImage) {
        boolean isCreateNotification = false;
        Blog beforeUpdateBlog = blogRepository.findById(blogId).orElseThrow();
        // 更新対象のブログの著者が他ユーザである場合エラー
        if (!Objects.equals(beforeUpdateBlog.getAuthor().getId(), userUtilService.getCurrentUser().getId())) {
            throw new IllegalStateException("他ユーザのブログを編集することはできません");
        }
        // 下書き状態のブログを公開する場合はブログ作成通知を登録する
        if (beforeUpdateBlog.getStatus().equals(BlogStatus.DRAFT) && input.getStatus().equals(BlogStatus.PUBLISHED)) {
            isCreateNotification = true;
        }

        String filePath = beforeUpdateBlog.getThumbnailUrl();

        // リクエストにサムネイル画像が含まれている場合保存する
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            // ファイル名を一意にするためにUUIDを使用する（ユーザIDも利用可能）
            String fileName = UUID.randomUUID().toString() + "_" + thumbnailImage.getOriginalFilename();

            // ファイルの保存処理
            filePath = storageService.saveFile(thumbnailImage, fileName);
        }
        // URLをブログエンティティにセット
        input.setThumbnailUrl(filePath);


        //DBを更新
        blogRepository.update(blogId, input);

        // 関連アーティスト情報を保存
        blogArtistService.saveBlogArtist(input, artistIdList);

        Blog updatedBlog = blogRepository.findById(blogId).orElseThrow();


        //Elasticsearchのインデックスを更新
//        blogSearchRepository.save(updatedBlog);

        entityManager.flush();
        entityManager.refresh(updatedBlog);

        // ブログ作成通知の登録
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
    public Integer updatedLikeCount(Long blogId, Boolean isCansel) {
        Blog blog = blogRepository.findById(blogId).orElseThrow();
        int updatedLikeCount;
        if (isCansel) {
            updatedLikeCount = blog.getLikeCount() > 0 ? blog.getLikeCount() - 1 : 0;
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
     * ブログを非公開にします。
     *
     * @param blogId    ブログID
     * @param updatedBy 更新者（ユーザID）
     * @throws IllegalArgumentException 指定されたブログが見つからない場合
     */
    @Transactional
    public void unpublishBlog(Long blogId, String updatedBy) {
        Blog targetBlog = blogRepository.findById(blogId).orElseThrow();
        // 更新対象のブログの著者が他ユーザである場合エラー
        if (!Objects.equals(targetBlog.getAuthor().getId(), userUtilService.getCurrentUser().getId())) {
            throw new IllegalStateException("他ユーザのブログを編集することはできません");
        }

        int updatedRows = blogRepository.unpublishBlog(blogId, updatedBy);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("指定されたブログが見つかりません: ID=" + blogId);
        }
    }

    /**
     * ブログ情報を削除します。
     *
     * @param blogId 削除対象のブログID
     */
    @Transactional
    public void deleteBlog(Long blogId) {
        Blog targetBlog = blogRepository.findById(blogId).orElseThrow();
        if (!targetBlog.getAuthor().equals(userUtilService.getCurrentUser())) {
            throw new IllegalStateException();
        }
        // DBのデータを削除
        blogRepository.delete(blogId, userUtilService.getCurrentUser().getId().toString());
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

    /**
     * ユーザとブログを指定していいねを登録するメソッド
     * すでにいいね済みの場合は登録を行わない
     *
     * @param userId ユーザID
     * @param blogId ブログID
     */
    @Transactional
    public Integer likeBlog(Long userId, Long blogId) {
        // すでにいいね済みか確認
        if (!userBlogLikeRepository.isLikeBlog(userId, blogId)) {
            // いいねを新規登録
            User currentUser = userRepository.findById(userId).orElseThrow();
            UserBlogLike userBlogLike = UserBlogLike.builder()
                    .id(new UserBlogLikeId(userId, blogId))
                    .user(currentUser)
                    .blog(blogRepository.findById(blogId).orElseThrow())
                    .createdBy(currentUser.getId().toString())
                    .updatedBy(currentUser.getId().toString())
                    .build();
            userBlogLikeRepository.save(userBlogLike);
            // いいね数を更新
            return updatedLikeCount(blogId, false);
        }
        return blogRepository.findById(blogId).orElseThrow().getLikeCount();
    }

    /**
     * 指定したユーザが指定したブログに対していいねをしているかを確認します。
     *
     * @param userId ユーザID
     * @param blogId ブログID
     * @return true いいねしている場合
     */
    public boolean isLikeBlog(Long userId, Long blogId) {
        return userBlogLikeRepository.isLikeBlog(userId, blogId);
    }

    /**
     * 指定したユーザとブログに対するいいねを削除します。
     *
     * @param userId ユーザID
     * @param blogId ブログID
     */
    @Transactional
    public Integer clearLikeBlog(Long userId, Long blogId) {
        // いいね削除
        userBlogLikeRepository.clearLikeBlog(userId, blogId);
        // ブログデータのいいね数更新
        return updatedLikeCount(blogId, true);
    }


}
