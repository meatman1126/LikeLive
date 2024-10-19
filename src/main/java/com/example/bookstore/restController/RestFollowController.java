package com.example.bookstore.restController;

import com.example.bookstore.dto.view.FollowViewDto;
import com.example.bookstore.service.FollowService;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Restフォローコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestFollowController {

    /**
     * フォローサービス
     */
    @Autowired
    private FollowService followService;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * ログインユーザが指定したユーザをフォローします。
     *
     * @param targetId フォローするユーザのID
     * @return 成功時はステータス200を返す
     */
    @PostMapping("/follow/{targetId}")
    public ResponseEntity<Void> followUser(@PathVariable Long targetId) {
        followService.followUser(targetId);
        return ResponseEntity.ok().build();
    }

    /**
     * ログインユーザが指定したユーザのフォローを解除します。
     *
     * @param targetId フォロー解除するユーザのID
     * @return 成功時はステータス200を返す
     */
    @PostMapping("/follow/cancel/{targetId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long targetId) {
        followService.unfollowUser(targetId);
        return ResponseEntity.ok().build();
    }

    /**
     * ログインユーザがフォロー中のユーザを取得します。
     *
     * @return フォローしているユーザのリスト
     */
    @GetMapping("/follow/followed")
    public ResponseEntity<List<FollowViewDto>> getFollowedUsers() {
        List<FollowViewDto> followedUsers = followService.getFollowedUsersInfo(userUtilService.getCurrentUser().getId());
        return ResponseEntity.ok(followedUsers);
    }

    /**
     * 多分使用しないと思う
     * 指定されたユーザがフォローしているユーザを取得します。
     *
     * @param targetUserId ユーザID
     * @return フォローしているユーザのリスト
     */
    @GetMapping("/follow/followed/{targetUserId}")
    public ResponseEntity<List<FollowViewDto>> getFollowedUsersByUserId(@PathVariable Long targetUserId) {
        List<FollowViewDto> followedUsers = followService.getFollowedUsersInfo(targetUserId);
        return ResponseEntity.ok(followedUsers);
    }

    /**
     * ログインユーザをフォローしているユーザを取得します。
     *
     * @return フォロワーのリスト
     */
    @GetMapping("/follow/followers")
    public ResponseEntity<List<FollowViewDto>> getFollowers() {
        List<FollowViewDto> followers = followService.getFollowersInfo(userUtilService.getCurrentUser().getId());
        return ResponseEntity.ok(followers);
    }

    /**
     * 指定されたユーザをフォローしているユーザを取得します。
     *
     * @param targetUserId ユーザID
     * @return フォロワーのリスト
     */
    @GetMapping("/follow/followers/{targetUserId}")
    public ResponseEntity<List<FollowViewDto>> getFollowersByUserId(@PathVariable Long targetUserId) {
        List<FollowViewDto> followers = followService.getFollowersInfo(targetUserId);
        return ResponseEntity.ok(followers);
    }
}
