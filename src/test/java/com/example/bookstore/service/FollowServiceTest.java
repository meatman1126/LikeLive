package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.dto.view.FollowViewDto;
import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.repository.jpa.FollowRepository;
import com.example.bookstore.repository.jpa.UserRepository;
import com.example.bookstore.service.util.UserUtilService;

class FollowServiceTest extends SpringBootTestBase {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserUtilService userUtilService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        currentUser = testDataSetup.createTestUser();
        targetUser = testDataSetup.createTestUser();
        
        // 認証情報の設定
        testDataSetup.setupAuthentication(currentUser);
    }

    @Test
    void followUser_正常系() {
        // テスト実行
        followService.followUser(targetUser.getId());

        // 検証
        assertTrue(followRepository.isFollowing(currentUser.getId(), targetUser.getId()));

        List<Notification> notifications = notificationService.getUnreadFollowNotifications(targetUser.getId(), currentUser.getId());
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals(targetUser, notifications.get(0).getTargetUser());
        assertEquals(currentUser, notifications.get(0).getTriggerUser());
    }

    @Test
    void unfollowUser_正常系() {
        // テストデータ準備
        Follow follow = testDataSetup.createTestFollow(currentUser, targetUser);
        Notification notification = testDataSetup.createTestNotification(targetUser, currentUser, NotificationType.FOLLOW, null, null, false);

        // テスト実行
        followService.unfollowUser(targetUser.getId());

        // 検証
        assertTrue(!followRepository.isFollowing(currentUser.getId(), targetUser.getId()));

        List<Notification> notifications = notificationService.getUnreadFollowNotifications(targetUser.getId(), currentUser.getId());
        assertTrue(notifications.isEmpty());
    }

    @Test
    void getFollowers_正常系() {
        // テストデータ準備
        User follower1 = testDataSetup.createTestUser();
        User follower2 = testDataSetup.createTestUser();
        testDataSetup.createTestFollow(follower1, currentUser);
        testDataSetup.createTestFollow(follower2, currentUser);

        // テスト実行
        List<User> followers = followService.getFollowers();

        // 検証
        assertNotNull(followers);
        assertEquals(2, followers.size());
        assertTrue(followers.contains(follower1));
        assertTrue(followers.contains(follower2));
    }

    @Test
    void getFollowedUsersInfo_正常系() {
        // テストデータ準備
        User followed1 = testDataSetup.createTestUser();
        User followed2 = testDataSetup.createTestUser();
        testDataSetup.createTestFollow(currentUser, followed1);
        testDataSetup.createTestFollow(currentUser, followed2);

        // テスト実行
        List<FollowViewDto> result = followService.getFollowedUsersInfo(currentUser.getId());

        // 検証
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getUser().equals(followed1)));
        assertTrue(result.stream().anyMatch(dto -> dto.getUser().equals(followed2)));
        assertTrue(result.stream().allMatch(dto -> dto.getIsFollowing()));
    }

    @Test
    void getFollowersInfo_正常系() {
        // テストデータ準備
        User follower1 = testDataSetup.createTestUser();
        User follower2 = testDataSetup.createTestUser();
        testDataSetup.createTestFollow(follower1, currentUser);
        testDataSetup.createTestFollow(follower2, currentUser);

        // テスト実行
        List<FollowViewDto> result = followService.getFollowersInfo(currentUser.getId());

        // 検証
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getUser().equals(follower1)));
        assertTrue(result.stream().anyMatch(dto -> dto.getUser().equals(follower2)));
        // フォロワーをフォローしていない場合はisFollowingがfalse
        assertTrue(result.stream().allMatch(dto -> !dto.getIsFollowing()));
    }

    @Test
    void followUser_対象ユーザが存在しない場合() {
        // テスト実行と検証
        assertThrows(RuntimeException.class, () -> followService.followUser(999L));
    }
} 