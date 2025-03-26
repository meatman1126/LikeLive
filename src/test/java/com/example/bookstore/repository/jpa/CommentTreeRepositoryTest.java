package com.example.bookstore.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.JpaTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.entity.CommentTree;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.key.CommentTreeId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class CommentTreeRepositoryTest extends JpaTestBase {

    @Autowired
    private CommentTreeRepository commentTreeRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Blog testBlog;
    private Comment parentComment;
    private Comment replyComment1;
    private Comment replyComment2;
    private Comment deletedReplyComment;

    @BeforeEach
    void setUp() {
        // テストデータのセットアップ
        testUser = testDataSetup.createTestUser();
        testBlog = testDataSetup.createTestBlog(testUser);
        parentComment = testDataSetup.createTestComment(testUser, testBlog, "親コメント");
        replyComment1 = testDataSetup.createTestComment(testUser, testBlog, "返信コメント1");
        replyComment2 = testDataSetup.createTestComment(testUser, testBlog, "返信コメント2");
        deletedReplyComment = testDataSetup.createTestComment(testUser, testBlog, "削除済み返信コメント");
        deletedReplyComment.setIsDeleted(true);

        // コメントツリーの作成
        CommentTree commentTree1 = CommentTree.builder()
                .id(new CommentTreeId(parentComment.getId(), replyComment1.getId()))
                .parentComment(parentComment)
                .replyComment(replyComment1)
                .replyNumber(1)
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .build();

        CommentTree commentTree2 = CommentTree.builder()
                .id(new CommentTreeId(parentComment.getId(), replyComment2.getId()))
                .parentComment(parentComment)
                .replyComment(replyComment2)
                .replyNumber(2)
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .build();

        CommentTree deletedCommentTree = CommentTree.builder()
                .id(new CommentTreeId(parentComment.getId(), deletedReplyComment.getId()))
                .parentComment(parentComment)
                .replyComment(deletedReplyComment)
                .replyNumber(3)
                .createdBy(testUser.getId().toString())
                .updatedBy(testUser.getId().toString())
                .build();

        commentTreeRepository.save(commentTree1);
        commentTreeRepository.save(commentTree2);
        commentTreeRepository.save(deletedCommentTree);
    }

    @Test
    @DisplayName("countRepliesByParentCommentId_正常系_返信が存在する場合")
    void countRepliesByParentCommentId_正常系_返信が存在する場合() {
        // 実行
        Integer result = commentTreeRepository.countRepliesByParentCommentId(parentComment.getId());

        // 検証
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("countRepliesByParentCommentId_正常系_返信が存在しない場合")
    void countRepliesByParentCommentId_正常系_返信が存在しない場合() {
        // 実行
        Integer result = commentTreeRepository.countRepliesByParentCommentId(999L);

        // 検証
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("findRepliesByParentCommentId_正常系_返信が存在する場合")
    void findRepliesByParentCommentId_正常系_返信が存在する場合() {
        // 実行
        List<Comment> result = commentTreeRepository.findRepliesByParentCommentId(parentComment.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting("content")
                .containsExactly("返信コメント1", "返信コメント2");
    }

    @Test
    @DisplayName("findRepliesByParentCommentId_正常系_返信が存在しない場合")
    void findRepliesByParentCommentId_正常系_返信が存在しない場合() {
        // 実行
        List<Comment> result = commentTreeRepository.findRepliesByParentCommentId(999L);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findRepliesByParentCommentId_正常系_論理削除された返信は除外される")
    void findRepliesByParentCommentId_正常系_論理削除された返信は除外される() {
        // 実行
        List<Comment> result = commentTreeRepository.findRepliesByParentCommentId(parentComment.getId());

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting("content")
                .containsExactly("返信コメント1", "返信コメント2");
        assertThat(result).extracting("id")
                .doesNotContain(deletedReplyComment.getId());
    }
}