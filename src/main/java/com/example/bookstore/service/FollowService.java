package com.example.bookstore.service;

import com.example.bookstore.dto.repository.FollowRepositoryDto;
import com.example.bookstore.dto.view.FollowViewDto;
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

/**
 * フォローサービス
 */
@Service
public class FollowService {

    /**
     * フォローリポジトリ
     */
    @Autowired
    private FollowRepository followRepository;

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    private UserUtilService userUtilService;

    /**
     * 通知サービス
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * ユーザリポジトリ
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザ間のフォロー関係を登録します。
     *
     * @param targetId フォロー対象のユーザID
     */
    @Transactional
    public void followUser(Long targetId) {
        //フォロー関係の登録
        User currentUser = userUtilService.getCurrentUser();
        Follow follow = Follow.builder()
                .follower(currentUser)
                .followed(userRepository.findById(targetId).orElseThrow())
                .followAt(LocalDateTime.now())
                .createdBy(currentUser.getId().toString())
                .updatedBy(currentUser.getId().toString())
                .build();

        followRepository.save(follow);

        //通知データの登録
        saveNotificationOfFollow(targetId);

    }


    /**
     * ユーザ間のフォロー関係を削除します。
     *
     * @param targetId フォロー解除対象のユーザID
     */
    @Transactional
    public void unfollowUser(Long targetId) {
        //フォロー関係の削除
        followRepository.unfollow(userUtilService.getCurrentUser().getId(), targetId);
        //通知データの削除（対象通知が未読の場合）
        deleteNotificationOfFollow(targetId);
    }

    /**
     * ログインユーザのフォロワーを取得します。
     *
     * @return ログインユーザのフォロワー
     */
    public List<User> getFollowers() {
        return followRepository.findFollowers(userUtilService.getCurrentUser().getId());
    }

    /**
     * 指定されたユーザがフォロー中のユーザを取得します。
     *
     * @param userId ユーザID
     * @return 指定されたユーザがフォローされているユーザ
     */
    public List<FollowViewDto> getFollowedUsersInfo(Long userId) {
        List<FollowRepositoryDto> repositoryDtoList = followRepository.findFollowedUsersInfo(userId);

        return FollowViewDto.build(repositoryDtoList);
    }

    /**
     * 指定したユーザのフォロワー情報を取得します。
     *
     * @param userId ユーザID
     * @return 指定したユーザがフォローしているユーザ
     */
    public List<FollowViewDto> getFollowersInfo(Long userId) {
        List<FollowRepositoryDto> repositoryDtoList = followRepository.findFollowersInfo(userId);

        return FollowViewDto.build(repositoryDtoList);
    }

    /**
     * 指定されたユーザに対するフォロー通知を作成します。
     *
     * @param targetId 通知対象ユーザ
     */
    private void saveNotificationOfFollow(Long targetId) {
        User triggerUser = userUtilService.getCurrentUser();
        Notification notification = Notification.builder()
                .targetUser(userRepository.findById(targetId).orElseThrow())
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
     * @param targetId 通知対象ユーザ
     */
    private void deleteNotificationOfFollow(Long targetId) {
        User currentUser = userUtilService.getCurrentUser();
        //削除されたフォローに関する通知を検索し未読なら削除する
        List<Notification> notifications = notificationService.getUnreadFollowNotifications(targetId, currentUser.getId());
        if (!notifications.isEmpty()) {
            notificationService.deleteNotifications(
                    notifications.stream().map(Notification::getId).collect(Collectors.toList())
            );
        }
    }

}
