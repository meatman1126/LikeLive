package com.example.bookstore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.BlogArtist;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.UserBlogLike;
import com.example.bookstore.entity.code.BlogCategory;
import com.example.bookstore.entity.code.BlogStatus;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.entity.key.CommentTreeId;
import com.example.bookstore.entity.key.UserArtistId;
import com.example.bookstore.entity.key.UserBlogLikeId;
import com.example.bookstore.repository.jpa.ArtistRepository;
import com.example.bookstore.repository.jpa.BlogArtistRepository;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.repository.jpa.CommentRepository;
import com.example.bookstore.repository.jpa.CommentTreeRepository;
import com.example.bookstore.repository.jpa.FollowRepository;
import com.example.bookstore.repository.jpa.NotificationRepository;
import com.example.bookstore.repository.jpa.UserArtistRepository;
import com.example.bookstore.repository.jpa.UserBlogLikeRepository;
import com.example.bookstore.repository.jpa.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@TestConfiguration
public class TestDataSetup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserArtistRepository userArtistRepository;

    @Autowired
    private UserBlogLikeRepository userBlogLikeRepository;

    @Autowired
    private BlogArtistRepository blogArtistRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentTreeRepository commentTreeRepository;
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public User createTestUser() {
        User user = User.builder().displayName("Test User").subject("test-subject-" + UUID.randomUUID().toString())
                .enabled(true).createdBy("Test").updatedBy("Test").build();
        return userRepository.save(user);
    }

    @Transactional
    public Blog createTestBlog(User author) {
        Blog blog = Blog.builder().title("Test Blog").content(Map.of("text", "Test Content")).author(author)
                .category(BlogCategory.DIARY).status(BlogStatus.PUBLISHED).isDeleted(false)
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).createdBy("Test")
                .updatedBy("Test").viewCount(0).build();
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog createTestDraftBlog(User author) {
        Blog blog = Blog.builder().title("Draft Blog").content(Map.of("text", "Draft Content")).author(author)
                .category(BlogCategory.DIARY).status(BlogStatus.DRAFT).isDeleted(false)
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).createdBy("Test")
                .updatedBy("Test").build();
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog createTestArchiveBlog(User author) {
        Blog blog = Blog.builder().title("Archived Blog").content(Map.of("text", "Archived Content")).author(author)
                .category(BlogCategory.DIARY).status(BlogStatus.ARCHIVED).isDeleted(false)
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).createdBy("Test")
                .updatedBy("Test").build();
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog createTestBlogWithTitle(User author, String title) {
        Blog blog = Blog.builder().title(title).content(Map.of("text", "Test Content")).author(author)
                .category(BlogCategory.DIARY).status(BlogStatus.PUBLISHED).isDeleted(false)
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).createdBy("Test")
                .updatedBy("Test").build();
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog createTestBlogWithContent(User author, String textContent) {
        Blog blog = Blog.builder().title("Test Blog").content(Map.of("text", textContent)).author(author)
                .category(BlogCategory.DIARY).status(BlogStatus.PUBLISHED).isDeleted(false)
                .blogCreatedTime(LocalDateTime.now()).blogUpdatedTime(LocalDateTime.now()).createdBy("Test")
                .updatedBy("Test").build();
        return blogRepository.save(blog);
    }

    @Transactional
    public Artist createTestArtist() {
        Artist artist = Artist.builder().id("test-artist-" + UUID.randomUUID().toString()).name("Test Artist")
                .createdBy("Test").updatedBy("Test").build();
        return artistRepository.save(artist);
    }

    @Transactional
    public UserArtist createTestUserArtist(User user, Artist artist) {
        UserArtist userArtist = UserArtist.builder().id(new UserArtistId(user.getId(), artist.getId())).user(user)
                .artist(artist).createdBy("Test").updatedBy("Test").build();
        return userArtistRepository.save(userArtist);
    }

    /**
     * テスト用のコメントを作成します。
     *
     * @param author コメントの作成者
     * @param blog コメントが属するブログ
     * @param content コメントの内容
     * @return 作成されたコメント
     */
    public Comment createTestComment(User author, Blog blog, String content) {
        Comment comment = Comment.builder().content(content).author(author).blog(blog)
                .commentCreatedTime(LocalDateTime.now()).commentUpdatedTime(LocalDateTime.now()).isDeleted(false)
                .createdBy(author.getId().toString()).updatedBy(author.getId().toString()).build();
        commentRepository.save(comment);
        entityManager.flush();
        entityManager.refresh(comment);
        return comment;
    }

        /**
     * テスト用の返信コメントを作成します。
     *
     * @param author コメントの作成者
     * @param blog コメントが属するブログ
     * @param parentComment 返信対象の親コメント
     * @return 作成された返信コメント
     */
    @Transactional
    public Comment createTestReplyComment(User author, Blog blog, Comment parentComment) {
        // 返信コメントを作成
        Comment replyComment = Comment.builder()
                .content("Test reply comment")
                .author(author)
                .blog(blog)
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .isDeleted(false)
                .createdBy(author.getId().toString())
                .updatedBy(author.getId().toString())
                .build();
        commentRepository.save(replyComment);

        // 親コメントの返信件数を取得
        Integer replyCount = commentTreeRepository.countRepliesByParentCommentId(parentComment.getId());

        // コメントツリーを作成
        CommentTree commentTree = CommentTree.builder()
                .id(new CommentTreeId(parentComment.getId(), replyComment.getId()))
                .parentComment(parentComment)
                .replyComment(replyComment)
                .replyNumber(replyCount + 1)
                .createdBy(author.getId().toString())
                .updatedBy(author.getId().toString())
                .build();
        commentTreeRepository.save(commentTree);

        entityManager.flush();
        entityManager.refresh(replyComment);
        return replyComment;
    }

    /**
     * テスト用のコメントツリーを作成します。
     *
     * @param parentComment 親コメント
     * @param replyComment 返信コメント
     * @param replyNumber 返信番号
     * @param createdBy 作成者ID
     * @return 作成されたコメントツリー
     */
    public CommentTree createTestCommentTree(Comment parentComment, Comment replyComment, int replyNumber,
            String createdBy) {
        CommentTree commentTree = CommentTree.builder()
                .id(new CommentTreeId(parentComment.getId(), replyComment.getId())).parentComment(parentComment)
                .replyComment(replyComment).replyNumber(replyNumber).createdBy(createdBy).updatedBy(createdBy).build();
        entityManager.persist(commentTree);
        entityManager.flush();
        entityManager.refresh(commentTree);
        return commentTree;
    }

    /**
     * テスト用のフォロー関係を作成します。
     *
     * @param follower フォローするユーザ
     * @param followed フォローされるユーザ
     * @return 作成されたフォロー関係
     */
    @Transactional
    public Follow createTestFollow(User follower, User followed) {
        Follow follow = Follow.builder().follower(follower).followed(followed).followAt(LocalDateTime.now())
                .createdBy(follower.getId().toString()).updatedBy(follower.getId().toString()).build();
        return followRepository.save(follow);
    }

    /**
     * テスト用の通知を作成します。
     *
     * @param targetUserId 通知対象のユーザID
     * @param triggerUserId 通知を発生させたユーザID
     * @param notificationType 通知タイプ
     * @param relatedBlog 通知に関連するブログ(NotificationType.BLOG_CREATEDまたはNotificationType.COMMENTのみ)
     * @param relatedComment 通知に関連するコメント(NotificationType.COMMENTのみ)
     * @param isRead 既読フラグ
     * @return 作成された通知
     */
    @Transactional
    public Notification createTestNotification(User targetUser, User triggerUser,
            NotificationType notificationType, Blog relatedBlog, Comment relatedComment, boolean isRead) {

        Notification notification = Notification.builder()
                .targetUser(targetUser)
                .triggerUser(triggerUser)
                .notificationType(notificationType)
                .relatedBlog(relatedBlog)
                .relatedComment(relatedComment)
                .notificationCreatedAt(LocalDateTime.now())
                .isRead(isRead)
                .isDeleted(false)
                .createdBy("Test")
                .updatedBy("Test")
                .build();
        return notificationRepository.save(notification);
    }

    /**
     * テスト用のユーザブログいいねを作成します。
     *
     * @param user いいねするユーザ
     * @param blog いいねされるブログ
     * @return 作成されたユーザブログいいね
     */
    @Transactional
    public UserBlogLike createTestUserBlogLike(User user, Blog blog) {
        UserBlogLike userBlogLike = UserBlogLike.builder().id(new UserBlogLikeId(user.getId(), blog.getId())).user(user)
                .blog(blog).createdBy(user.getId().toString()).updatedBy(user.getId().toString()).build();
        return userBlogLikeRepository.save(userBlogLike);
    }

    /**
     * テスト用のブログアーティスト関連を作成します。
     *
     * @param blog ブログ
     * @param artist アーティスト
     * @return 作成されたブログアーティスト関連
     */
    @Transactional
    public BlogArtist createTestBlogArtist(Blog blog, Artist artist) {
        BlogArtist blogArtist = BlogArtist.builder().blog(blog).artist(artist)
                .createdBy(blog.getAuthor().getId().toString()).updatedBy(blog.getAuthor().getId().toString()).build();
        return blogArtistRepository.save(blogArtist);
    }

    /**
     * 指定されたユーザの認証情報を設定します。
     * @param user 認証情報を設定するユーザ
     */
    public void setupAuthentication(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * テスト用のユーザを作成します。
     *
     * @param subject ユーザのsubject
     * @param displayName ユーザの表示名
     * @return 作成されたユーザ
     */
    @Transactional
    public User createUser(String subject, String displayName) {
        User user = User.builder()
                .displayName(displayName)
                .subject(subject)
                .enabled(true)
                .createdBy("Test")
                .updatedBy("Test")
                .build();
        return userRepository.save(user);
    }
}