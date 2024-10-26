package com.example.bookstore.service;

import com.example.bookstore.dto.repository.ParentCommentRepositoryDto;
import com.example.bookstore.dto.view.ParentCommentViewDto;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.entity.key.CommentTreeId;
import com.example.bookstore.repository.jpa.CommentRepository;
import com.example.bookstore.repository.jpa.CommentTreeRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * コメントサービス
 */
@Service
public class CommentService {

    /**
     * コメントリポジトリ
     */
    @Autowired
    private CommentRepository commentRepository;

    /**
     * コメントツリーリポジトリ
     */
    @Autowired
    private CommentTreeRepository commentTreeRepository;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * 通知サービス
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * 指定されたブログに対する親コメントを全件取得します。
     * コメントは親コメントIDおよび返信順の昇順にソートされます。
     *
     * @param blogId ブログID
     * @return コメントリスト
     */
    public List<ParentCommentViewDto> getCommentsByBlogId(Long blogId) {
        List<ParentCommentRepositoryDto> repositoryDtoList = commentRepository.findParentCommentsByBlogId(blogId);
        return ParentCommentViewDto.build(repositoryDtoList);
    }

    /**
     * 指定されたコメントの子コメント（返信）を取得します。
     *
     * @param parentId 親コメントID
     * @return コメントリスト
     */
    public List<Comment> getCommentsAndRepliesByParentId(Long parentId) {
        return commentTreeRepository.findRepliesByParentCommentId(parentId);
    }


    /**
     * コメントを新規登録します。
     *
     * @param comment 登録されるコメント情報
     * @return 登録されたコメント情報
     */
    @Transactional
    public Comment registerComment(Comment comment) {
        //コメントの新規登録
        Comment createdComment = commentRepository.save(comment);
        //コメント作成通知の登録
        saveNotificationOfComment(comment);

        return createdComment;
    }

    /**
     * 返信コメントを登録します。
     *
     * @param parentCommentId 返信対象の親コメントID
     * @param replyComment    返信コメント情報
     * @return 登録された返信コメント
     */
    @Transactional
    public Comment registerReplyComment(Long parentCommentId, Comment replyComment) {
        //返信コメントをcommentテーブルに登録
        Comment registerdComment = registerComment(replyComment);

        //親コメントの返信件数を取得
        Integer replyCount = commentTreeRepository.countRepliesByParentCommentId(parentCommentId);
        //commentTreeテーブルに親コメントと返信コメントの関連を登録
        CommentTree commentTree = CommentTree.builder()
                .id(new CommentTreeId(parentCommentId, registerdComment.getId()))
                .parentComment(commentRepository.findById(parentCommentId).orElseThrow())
                .replyComment(registerdComment)
                .replyNumber(replyCount + 1)
                .createdBy(userUtilService.getCurrentUserId())
                .updatedBy(userUtilService.getCurrentUserId())
                .build();
        commentTreeRepository.save(commentTree);

        return registerdComment;
    }

    /**
     * 指定されたコメントを更新します。
     *
     * @param id      更新対象のコメントID
     * @param content 更新するコンテンツ情報
     * @return 更新されたコメント情報
     */
    @Transactional
    public Comment updateComment(Long id, String content) {
        LocalDateTime updatedTime = LocalDateTime.now();

        commentRepository.updateCommentContent(id, content, updatedTime, userUtilService.getCurrentUserId());
        return commentRepository.findById(id).orElseThrow();  // 1件以上更新されたかをチェック
    }

    /**
     * 指定されたコメントを削除します。
     *
     * @param id 削除対象のコメントID
     * @return 削除成功時にtrue、失敗時にfalseを返す
     */
    @Transactional
    public Boolean deleteComment(Long id) {
        //コメントの削除処理
        int deletedRows = commentRepository.deleteCommentById(id, userUtilService.getCurrentUserId());
        //削除されたコメントに関する通知の削除
        deleteNotificationOfComment(id);

        return deletedRows > 0;
    }

    /**
     * 指定されたIDのコメントを取得するメソッド
     *
     * @param id コメントID
     * @return 指定されたコメント情報
     */
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    /**
     * 指定されたコメントの通知を作成します。
     *
     * @param comment 通知の対象となるコメント
     */
    private void saveNotificationOfComment(Comment comment) {
        User triggerUser = userUtilService.getCurrentUser();
        Notification notification = Notification.builder()
                .targetUser(comment.getBlog().getAuthor())
                .notificationType(NotificationType.COMMENT)
                .relatedBlog(comment.getBlog())
                .relatedComment(comment)
                .notificationCreatedAt(LocalDateTime.now())
                .isRead(false)
                .triggerUser(triggerUser)
                .isDeleted(false)
                .createdBy(triggerUser.getId().toString())
                .updatedBy(triggerUser.getId().toString())
                .build();

        notificationService.saveNotification(notification);
    }

    /**
     * 指定されたコメントの未読通知を削除します。
     *
     * @param commentId コメントID
     */
    private void deleteNotificationOfComment(Long commentId) {
        //削除されたフォローに関する通知を検索し未読なら削除する
        List<Notification> notifications = notificationService.getUnreadNotificationsByCommentId(commentId);
        if (!notifications.isEmpty()) {
            notificationService.deleteNotifications(
                    notifications.stream().map(Notification::getId).collect(Collectors.toList()));
        }
    }

}
