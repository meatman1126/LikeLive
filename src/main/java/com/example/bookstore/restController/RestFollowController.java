package com.example.bookstore.restController;

import com.example.bookstore.entity.User;
import com.example.bookstore.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestFollowController {

    @Autowired
    private FollowService followService;

    /**
     * ログインユーザが指定したユーザをフォローします。
     *
     * @param followedId フォローするユーザのID
     * @return 成功時はステータス200を返す
     */
    @PostMapping("/follow/{followedId}")
    public ResponseEntity<Void> followUser(@PathVariable Long followedId) {
        followService.followUser(followedId);
        return ResponseEntity.ok().build();
    }

    /**
     * ログインユーザが指定したユーザのフォローを解除します。
     *
     * @param followedId フォロー解除するユーザのID
     * @return 成功時はステータス200を返す
     */
    @PostMapping("/follow/cancel/{followedId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long followedId) {
        followService.unfollowUser(followedId);
        return ResponseEntity.ok().build();
    }

    /**
     * ログインユーザがフォローしているユーザを取得します。
     *
     * @return フォローしているユーザのリスト
     */
    @GetMapping("/follow/followed")
    public ResponseEntity<List<User>> getFollowedUsers() {
        List<User> followedUsers = followService.getFollowedUsers();
        return ResponseEntity.ok(followedUsers);
    }

    /**
     * 指定されたユーザがフォローしているユーザを取得します。
     *
     * @param userId ユーザID
     * @return フォローしているユーザのリスト
     */
    @GetMapping("/follow/followed/{userId}")
    public ResponseEntity<List<User>> getFollowedUsersByUserId(@PathVariable Long userId) {
        List<User> followedUsers = followService.getFollowedUsers(userId);
        return ResponseEntity.ok(followedUsers);
    }

    /**
     * ログインユーザをフォローしているユーザを取得します。
     *
     * @return フォロワーのリスト
     */
    @GetMapping("/follow/followers")
    public ResponseEntity<List<User>> getFollowers() {
        List<User> followers = followService.getFollowers();
        return ResponseEntity.ok(followers);
    }

    /**
     * 指定されたユーザをフォローしているユーザを取得します。
     *
     * @param userId ユーザID
     * @return フォロワーのリスト
     */
    @GetMapping("/follow/followers/{userId}")
    public ResponseEntity<List<User>> getFollowersByUserId(@PathVariable Long userId) {
        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }
}
