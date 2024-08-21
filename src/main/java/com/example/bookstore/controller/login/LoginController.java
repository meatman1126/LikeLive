package com.example.bookstore.controller.login;

import com.example.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String clientId;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("clientId", "google");
        System.out.println(LocalDateTime.now());
        return "login";  // login.htmlを返す
    }

    /**
     * ログイン成功後処理(Idp利用時)
     * 初回ログインユーザの場合DB登録を実施し、表示名の設定に遷移させる
     */
    @RequestMapping("/login/oauth")
    public String IdpLogin(@AuthenticationPrincipal OidcUser user, RedirectAttributes redirectAttributes) {
        if (!userService.isRegistered(user)) {
            userService.register(user);
            redirectAttributes.addAttribute("firstLogin", true);
            return "redirect:" + "/index";
        }

        return "redirect:" + "/index";
    }

    /**
     * ログイン成功後処理(Idp非利用時)
     */
    @RequestMapping("/login/success")
    public String successLogin() {
        return "redirect:" + "/index";
    }

    @RequestMapping("/login/error")
    public String failureLogin(Model model) {
        model.addAttribute("error", "メールアドレスまたはパスワードに誤りがあります。");
        return "login";
    }

}
