package com.example.bookstore.restController;

import com.example.bookstore.Exception.UserNotFoundException;
import com.example.bookstore.dto.form.user.UserDeleteForm;
import com.example.bookstore.dto.form.user.UserRegistrationForm;
import com.example.bookstore.dto.form.user.UserUpdateForm;
import com.example.bookstore.dto.form.user.UsersDeleteForm;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestUserController {

    @Autowired
    UserService userService;

    @Autowired
    UserUtilService userUtilService;

    /**
     * ユーザ情報を更新します。
     *
     * @param formData ユーザ情報（ユーザ名および好きなアーティスト）
     * @return ユーザ更新処理の結果を返します。
     */
    @PostMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody UserRegistrationForm formData) {
        User updatedUser = userService.initialUpdate(formData);
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
     * 指定されたユーザ情報を取得します。
     *
     * @param id 対象のユーザID
     * @return 指定されたユーザ情報
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        try {
            User user = userService.getUserInfo(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ユーザプロフィール情報を更新します。
     *
     * @param form ユーザ更新情報
     * @return 更新後のユーザ情報
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<User> updateUserProfile(
            @RequestBody UserUpdateForm form) {

        User updatedUser = userService.updateUserProfile(form.getDisplayName(), form.getSelfIntroduction(), form.getProfileImageUrl());
        return ResponseEntity.ok(updatedUser);
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


}
