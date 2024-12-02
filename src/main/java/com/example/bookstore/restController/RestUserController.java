package com.example.bookstore.restController;

import com.example.bookstore.Exception.UserNotFoundException;
import com.example.bookstore.dto.GoogleUserInfo;
import com.example.bookstore.dto.form.user.UserDeleteForm;
import com.example.bookstore.dto.form.user.UserRegistrationForm;
import com.example.bookstore.dto.form.user.UserUpdateForm;
import com.example.bookstore.dto.form.user.UsersDeleteForm;
import com.example.bookstore.dto.view.ProfileViewDto;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.GoogleService;
import com.example.bookstore.service.UserService;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Restユーザコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestUserController {

    /**
     * ユーザサービス
     */
    @Autowired
    UserService userService;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    UserUtilService userUtilService;

    /**
     * Googleサービス
     */
    @Autowired
    private GoogleService googleService;

    /**
     * ログインしたユーザ情報を取得します。初回ログインでDB未登録の場合DB登録を行います。
     * ログイン後に呼び出されることを想定しています。
     *
     * @param authorizationHeader ヘッダーに含まれる認証情報
     * @return ログインユーザ情報
     */
    @PostMapping("/login/after")
    public ResponseEntity<User> getLoginUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // DBにユーザが存在するか確認
            User user = userUtilService.getCurrentUser();

            if (user == null) {
                // ユーザが存在しない場合、新規に登録

                // AuthorizationヘッダーからBearerトークンを抽出
                String accessToken = extractToken(authorizationHeader);

                // Google APIを呼び出してユーザ情報を取得
                GoogleUserInfo googleUserInfo = googleService.getUserInfo(accessToken);
                user = User.builder()
                        .displayName(googleUserInfo.getGivenName())
                        .subject(googleUserInfo.getSub())
                        .enabled(true)
                        .createdBy("System")
                        .updatedBy("System")
                        .build();
                userService.register(user);
            }
            // ユーザ情報を返却
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // エラーが発生した場合、適切なステータスコードとメッセージを返却
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * ユーザ情報を更新します。(初回更新想定)
     *
     * @param formData     ユーザ情報（ユーザ名および好きなアーティスト）
     * @param profileImage プロフィール画像ファイル
     * @return ユーザ更新処理の結果を返します。
     */
    @PostMapping("/user/update/initial")
    public ResponseEntity<User> updateUser(
            @ModelAttribute UserRegistrationForm formData,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        User updatedUser = userService.initialUpdate(formData, profileImage);
//        User updatedUser = userService.getUserInfo(userUtilService.getCurrentUser().getId());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 現在ログインしているユーザ情報を取得します。
     *
     * @return ログイン中のユーザ情報
     */
    @GetMapping("/user/me")
    public ResponseEntity<User> getCurrentUser() {
        try {
            User currentUser = userUtilService.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 現在ログインしているユーザ情報を取得します。
     *
     * @return ログイン中のユーザ情報
     */
    @GetMapping("/user/my")
    public ResponseEntity<User> getUser() {
        try {
            User currentUser = userService.findBySubject(
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            return ResponseEntity.ok(currentUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 指定されたユーザ情報を取得します。
     *
     * @param id 対象のユーザID
     * @return 指定されたユーザ情報
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(auth.getPrincipal());
            User user = userService.getUserInfo(id);
//            User user1 = userUtilService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ログインユーザのプロフィール、アーティスト、およびブログ情報を取得します
     *
     * @return ResponseEntity<ProfileRepositoryDto> ユーザのプロフィール情報
     */
    @GetMapping("/user/profile")
    public ResponseEntity<ProfileViewDto> getUserProfile() {
        ProfileViewDto profile = userService.getUserProfile(userUtilService.getCurrentUser().getId(), false);
        return ResponseEntity.ok(profile);
    }

    /**
     * 指定されたユーザのプロフィール、アーティスト、およびブログ情報を取得します
     *
     * @param targetUserId プロフィールを取得したいユーザID
     * @return ResponseEntity<ProfileRepositoryDto> 指定されたユーザのプロフィール情報
     */
    @GetMapping("/user/profile/{targetUserId}")
    public ResponseEntity<ProfileViewDto> getOthersProfile(@PathVariable Long targetUserId) {
        boolean isOthersInfo = !targetUserId.equals(userUtilService.getCurrentUser().getId());
        ProfileViewDto profile = userService.getUserProfile(targetUserId, isOthersInfo);
        return ResponseEntity.ok(profile);
    }

    /**
     * キーワードに合致するユーザを取得します。
     *
     * @param keyword 検索キーワード
     * @return 該当するユーザリスト
     */
    @GetMapping("/user/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam("keyword") String keyword) {

        List<User> users = userService.searchUser(keyword);
        return ResponseEntity.ok(users);
    }

    // ソート条件の指定にも対応できるためのメソッド（現状はソート指定は行わない）
    private Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "createdUt");
    }


    /**
     * ユーザプロフィール情報を更新します。
     *
     * @param form         ユーザ更新情報
     * @param profileImage プロフィール画像
     * @return 更新後のユーザ情報
     */
    @PostMapping("/user/update")
    public ResponseEntity<ProfileViewDto> updateUserProfile(
            @ModelAttribute UserUpdateForm form,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        User updatedUser = userService.updateUserProfile(form, profileImage);
        ProfileViewDto profile = userService.getUserProfile(userUtilService.getCurrentUser().getId(), false);

        return ResponseEntity.ok(profile);
    }

    /**
     * 指定されたユーザ情報（1件）を削除します。
     *
     * @param form 削除対象のユーザID
     * @return ユーザ削除処理結果
     */
    @PostMapping("/user/delete")
    public ResponseEntity<User> deleteUser(@RequestBody UserDeleteForm form) {
        userService.deleteUser(form.getId());
        return ResponseEntity.ok().build();

    }

    /**
     * 指定されたユーザ情報（複数件）を削除します。
     *
     * @param form 削除対象のユーザID
     * @return ユーザ削除処理結果
     */
    @PostMapping("/users/delete")
    @Transactional
    public ResponseEntity<User> deleteUsers(@RequestBody UsersDeleteForm form) {
        for (Long id : form.getIdList()) {
            userService.deleteUser(id);
        }

        return ResponseEntity.ok().build();
    }

    // AuthorizationヘッダーからBearerトークンを抽出するヘルパーメソッド
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
    }


}
