package com.example.bookstore.service;

import com.example.bookstore.entity.Follow;
import com.example.bookstore.entity.Notification;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.code.NotificationType;
import com.example.bookstore.repository.jpa.FollowRepository;
import com.example.bookstore.repository.jpa.UserRepository;
import com.example.bookstore.service.util.UserUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserUtilService userUtilService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザ間のフォロー関係を登録します。
     *
     * @param followedId フォロー対象のユーザID
     */
    @Transactional
    public void followUser(Long followedId) {
        //フォロー関係の登録
        Follow follow = Follow.builder()
                .follower(userUtilService.getCurrentUser())
                .followed(userRepository.findById(followedId).orElseThrow())
                .followAt(LocalDateTime.now())
                .build();

        followRepository.save(follow);

        //通知データの登録
        saveNotificationOfFollow(followedId);

    }


    /**
     * ユーザ間のフォロー関係を削除します。
     *
     * @param followedId フォロー解除対象のユーザID
     */
    @Transactional
    public void unfollowUser(Long followedId) {
        //フォロー関係の削除
        followRepository.unfollow(userUtilService.getCurrentUser().getId(), followedId);
        //通知データの削除（対象通知が未読の場合）
        deleteNotificationOfFollow(followedId);
    }

    /**
     * 指定されたユーザがフォローされているユーザを取得します。
     *
     * @param userId ユーザID
     * @return 指定されたユーザがフォローされているユーザ
     */
    public List<User> getFollowedUsers(Long userId) {
        return followRepository.findFollowedUsers(userId);
    }

    /**
     * ログインユーザがフォローされているユーザを取得します。
     *
     * @return ログインユーザがフォローされているユーザ
     */
    public List<User> getFollowedUsers() {
        return getFollowedUsers(userUtilService.getCurrentUser().getId());
    }


    /**
     * 指定したユーザがフォローしているユーザを取得します。
     *
     * @param userId ユーザID
     * @return 指定したユーザがフォローしているユーザ
     */
    public List<User> getFollowers(Long userId) {
        return followRepository.findFollowers(userId);
    }

    /**
     * ログインユーザがフォローしているユーザを取得します。
     *
     * @return ログインユーザがフォローしているユーザ
     */
    public List<User> getFollowers() {
        return getFollowers(userUtilService.getCurrentUser().getId());
    }

    /**
     * 指定されたユーザに対するフォロー通知を作成します。
     *
     * @param followedId 通知対象ユーザ
     */
    private void saveNotificationOfFollow(Long followedId) {
        User triggerUser = userUtilService.getCurrentUser();
        Notification notification = Notification.builder()
                .targetUser(userRepository.findById(followedId).orElseThrow())
                .notificationType(NotificationType.FOLLOW)
                .relatedBlog(null)
                .notificationCreatedAt(LocalDateTime.now())
                .isRead(false)
                .triggerUser(triggerUser)
                .isDeleted(false)
                .createdBy(triggerUser.getId().toString())
                .updatedBy(triggerUser.getId().toString())
                .build();

        notificationService.saveNotification(notification);
    }


    /**
     * 指定されたユーザに対するフォロー通知が未読の場合削除します。
     *
     * @param followedId 通知対象ユーザ
     */
    private void deleteNotificationOfFollow(Long followedId) {
        User currentUser = userUtilService.getCurrentUser();
        //削除されたフォローに関する通知を検索し未読なら削除する
        List<Notification> notifications = notificationService.getUnreadFollowNotifications(followedId, currentUser.getId());
        if (!notifications.isEmpty()) {
            notificationService.deleteNotifications(
                    notifications.stream().map(Notification::getId).collect(Collectors.toList())
            );
        }
    }

}
