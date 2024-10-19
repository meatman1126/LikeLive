package com.example.bookstore.restController;

import com.example.bookstore.dto.form.comment.CommentDeleteForm;
import com.example.bookstore.dto.form.comment.CommentRegistrationForm;
import com.example.bookstore.dto.form.comment.CommentUpdateForm;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.service.CommentService;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Restコメントコントローラ
 */
@RestController
public class RestCommentController {

    /**
     * コメントサービス
     */
    @Autowired
    private CommentService commentService;

    /**
     * ブログリポジトリ
     */
    @Autowired
    private BlogRepository blogRepository;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * 指定されたブログに対するコメントを取得します。
     *
     * @param blogId ブログID
     * @return コメントリスト
     */
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<Comment>> getCommentsByBlogId(@PathVariable Long blogId) {
        List<Comment> comments = commentService.getCommentsByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 指定されたコメントとその返信を取得します。
     *
     * @param parentId 親コメントID
     * @return コメントリスト
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Comment>> getCommentsAndRepliesByParentId(@PathVariable Long parentId) {
        List<Comment> comments = commentService.getCommentsAndRepliesByParentId(parentId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 新規コメントを登録するエンドポイント
     *
     * @param form 新規登録するコメント情報
     * @return 登録されたコメント
     */
    @PostMapping("/comment/create")
    public ResponseEntity<Comment> registerComment(@RequestBody CommentRegistrationForm form) {
        Comment input = Comment.builder()
                .content(form.getContent())
                .blog(blogRepository.findById(form.getBlogId()).orElseThrow())
                .author(userUtilService.getCurrentUser())
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .isDeleted(false)
                .createdBy(userUtilService.getCurrentUserId())
                .updatedBy(userUtilService.getCurrentUserId())
                .build();
        Comment createdComment = commentService.registerComment(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    /**
     * 返信コメントを登録するエンドポイント
     *
     * @param form 新規登録するコメント情報
     * @return 登録されたコメント
     */
    @PostMapping("/comment/reply")
    public ResponseEntity<Comment> replyComment(@RequestBody CommentRegistrationForm form) {
        Comment input = Comment.builder()
                .content(form.getContent())
                .blog(blogRepository.findById(form.getBlogId()).orElseThrow())
                .author(userUtilService.getCurrentUser())
                .commentCreatedTime(LocalDateTime.now())
                .commentUpdatedTime(LocalDateTime.now())
                .isDeleted(false)
                .createdBy(userUtilService.getCurrentUserId())
                .updatedBy(userUtilService.getCurrentUserId())
                .build();
        Comment createdComment = commentService.registerReplyComment(form.getParentCommentId(), input);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    /**
     * コメントを更新するエンドポイント
     *
     * @param form 更新するコメント情報
     * @return 更新されたコメント情報
     */
    @PostMapping("/comment/update")
    public ResponseEntity<Comment> updateComment(
            @RequestBody CommentUpdateForm form) {
        Comment updatedComment = commentService.updateComment(form.getCommentId(), form.getContent());
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * コメントを削除するエンドポイント
     *
     * @param form 削除対象のコメントID
     * @return 削除成功時に200 OK、失敗時に404 Not Found
     */
    @PostMapping("/comment/delete")
    public ResponseEntity<Void> deleteComment(@RequestBody CommentDeleteForm form) {
        Boolean isDeleted = commentService.deleteComment(form.getCommentId());
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 指定されたIDのコメントを取得するエンドポイント
     *
     * @param id コメントID
     * @return 指定されたコメント情報
     */
    @GetMapping("/comment/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
}
