package com.example.bookstore.restController;

import com.example.bookstore.dto.form.UserRegistrationForm;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestUserController {

    @Autowired
    UserService userService;

    /**
     * ユーザ情報を更新します。
     *
     * @param formData ユーザ情報（ユーザ名および好きなアーティスト）
     * @return ユーザ更新処理の結果を返します。
     */
    @PostMapping("/register")
    public User registerUser(@RequestBody UserRegistrationForm formData) {
        // ここでリクエストを処理します
        System.out.println("Received registration request: " + formData);

        // 成功した場合のレスポンス
        return userService.update(formData);
    }
}
