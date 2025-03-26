package com.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.multipart.MultipartFile;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.form.user.UserRegistrationForm;
import com.example.bookstore.dto.form.user.UserUpdateForm;
import com.example.bookstore.dto.view.ProfileViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.UserNotFoundException;
import com.example.bookstore.repository.jpa.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

class UserServiceTest extends SpringBootTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    @PersistenceContext
    private EntityManager entityManager;

    private User currentUser;
    private User otherUser;
    private Artist testArtist1;
    private Artist testArtist2;
    private Blog testBlog;
    private MultipartFile testProfileImage;

    @BeforeEach
    void setUp() {
        // テストデータの準備
        currentUser = testDataSetup.createUser("test-user", "Test User");
        otherUser = testDataSetup.createUser("other-user", "Other User");

        // 認証情報の設定
        testDataSetup.setupAuthentication(currentUser);

        // アーティストの準備
        testArtist1 = testDataSetup.createTestArtist();
        testArtist2 = testDataSetup.createTestArtist();

        // ブログの準備
        testBlog = testDataSetup.createTestBlog(currentUser);

        // プロフィール画像の準備
        testProfileImage = new MockMultipartFile(
            "profileImage",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    @Test
    void getUserInfo_正常系() {
        // ユーザ情報を取得
        User user = userService.getUserInfo(currentUser.getId());

        // 検証
        assertThat(user).isEqualTo(currentUser);
    }

    @Test
    void getUserInfo_異常系_ユーザが存在しない() {
        // 存在しないユーザIDで検索
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserInfo(999L);
        });
    }

    @Test
    void findBySubject_正常系() {
        // subjectでユーザを検索
        User user = userService.findBySubject(currentUser.getSubject());

        // 検証
        assertThat(user).isEqualTo(currentUser);
    }

    @Test
    void findBySubject_異常系_ユーザが存在しない() {
        // 存在しないsubjectで検索
        User user = userService.findBySubject("non-existent-subject");

        // 検証
        assertThat(user).isNull();
    }

    @Test
    void isRegistered_正常系_登録済み() {
        // OidcUserのモックを作成
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getAttribute("sub")).thenReturn(currentUser.getSubject());

        // 登録済みかチェック
        Boolean isRegistered = userService.isRegistered(oidcUser);

        // 検証
        assertThat(isRegistered).isTrue();
    }

    @Test
    void isRegistered_正常系_未登録() {
        // OidcUserのモックを作成
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getAttribute("sub")).thenReturn("non-existent-subject");

        // 登録済みかチェック
        Boolean isRegistered = userService.isRegistered(oidcUser);

        // 検証
        assertThat(isRegistered).isFalse();
    }

    @Test
    void getUserProfile_正常系_自分のプロフィール() {
        // 自分のプロフィールを取得
        ProfileViewDto profile = userService.getUserProfile(currentUser.getId(), false);

        // 検証
        assertThat(profile.getUserId()).isEqualTo(currentUser.getId());
        assertThat(profile.getDisplayName()).isEqualTo(currentUser.getDisplayName());
        assertThat(profile.getIsFollow()).isNull();
    }

    @Test
    void getUserProfile_正常系_他ユーザのプロフィール() {
        // フォロー関係を作成
        testDataSetup.createTestFollow(currentUser, otherUser);
        entityManager.flush();

        // 他ユーザのプロフィールを取得
        ProfileViewDto profile = userService.getUserProfile(otherUser.getId(), true);

        // 検証
        assertThat(profile.getUserId()).isEqualTo(otherUser.getId());
        assertThat(profile.getDisplayName()).isEqualTo(otherUser.getDisplayName());
        assertThat(profile.getIsFollow()).isTrue();
    }

    @Test
    void searchUser_正常系() {
        // ユーザを検索
        List<User> users = userService.searchUser("Other");

        // 検証
        assertThat(users).contains(otherUser);
    }
    @Test
    void searchUser_正常系_自身は検索対象にならない() {
        // ユーザを検索
        List<User> users = userService.searchUser("Test");

        // 検証
        assertThat(users).doesNotContain(currentUser);
    }

    @Test
    void register_OidcUser_正常系() {
        // OidcUserのモックを作成
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getAttribute("given_name")).thenReturn("New User");
        when(oidcUser.getAttribute("sub")).thenReturn("new-subject");

        // ユーザを登録
        User registeredUser = userService.register(oidcUser);

        // 検証
        assertThat(registeredUser.getDisplayName()).isEqualTo("New User");
        assertThat(registeredUser.getSubject()).isEqualTo("new-subject");
    }

    @Test
    void register_User_正常系() {
        // ユーザを作成
        User newUser = User.builder()
                .displayName("New User")
                .subject("new-subject")
                .enabled(true)
                .createdBy("Test")
                .updatedBy("Test")
                .build();

        // ユーザを登録
        User registeredUser = userService.register(newUser);

        // 検証
        assertThat(registeredUser.getDisplayName()).isEqualTo("New User");
        assertThat(registeredUser.getSubject()).isEqualTo("new-subject");
    }

    @Test
    void initialUpdate_正常系() {
        // 登録フォームを作成
        UserRegistrationForm form = UserRegistrationForm.builder()
                .userName("Updated Name")
                .artistList(List.of(testArtist1, testArtist2))
                .build();

        // 初期更新を実行
        User updatedUser = userService.initialUpdate(form, testProfileImage);
        entityManager.flush();
        entityManager.refresh(updatedUser);

        // 検証
        assertThat(updatedUser.getDisplayName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getProfileImageUrl()).isNotNull();
    }

    @Test
    void updateUserProfile_正常系() {
        // 更新フォームを作成
        UserUpdateForm form = UserUpdateForm.builder()
                .displayName("Updated Name")
                .selfIntroduction("Test Introduction")
                .favoriteArtistList(List.of(testArtist1, testArtist2))
                .build();

        // プロフィールを更新
        User updatedUser = userService.updateUserProfile(form, testProfileImage);
        entityManager.flush();
        entityManager.refresh(updatedUser);

        // 検証
        assertThat(updatedUser.getDisplayName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getSelfIntroduction()).isEqualTo("Test Introduction");
        assertThat(updatedUser.getProfileImageUrl()).isNotNull();
    }

    @Test
    void deleteUser_正常系() {
        // ユーザを削除
        userService.deleteUser(currentUser.getId());
        entityManager.flush();
        entityManager.refresh(currentUser);

        // 検証
        User deletedUser = userRepository.findById(currentUser.getId()).orElseThrow();
        assertThat(deletedUser.getEnabled()).isFalse();

    }
} 