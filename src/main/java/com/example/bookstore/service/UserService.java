package com.example.bookstore.service;

import com.example.bookstore.Exception.UserNotFoundException;
import com.example.bookstore.dto.form.user.UserRegistrationForm;
import com.example.bookstore.dto.form.user.UserUpdateForm;
import com.example.bookstore.dto.view.ProfileViewDto;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.Blog;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import com.example.bookstore.repository.jpa.BlogRepository;
import com.example.bookstore.repository.jpa.FollowRepository;
import com.example.bookstore.repository.jpa.UserArtistRepository;
import com.example.bookstore.repository.jpa.UserRepository;
import com.example.bookstore.service.util.StorageService;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * ユーザサービスクラス
 */
@Service
public class UserService {

    /**
     * ユーザユーティルサービス
     */
    @Autowired
    UserUtilService userUtilService;

    /**
     * ユーザリポジトリ
     */
    @Autowired
    UserRepository userRepository;

    /**
     * アーティストサービス
     */
    @Autowired
    ArtistService artistService;

    /**
     * ユーザアーティストリポジトリ
     */
    @Autowired
    UserArtistRepository userArtistRepository;

    /**
     * ブログリポジトリ
     */
    @Autowired
    BlogRepository blogRepository;

    /**
     * フォローリポジトリ
     */
    @Autowired
    FollowRepository followRepository;

    /**
     * ストレージサービス
     */
    @Autowired
    StorageService storageService;

    /**
     * エンティティマネージャ
     */
    @Autowired
    private EntityManager entityManager;


    /**
     * 指定されたユーザ情報を取得します。
     *
     * @param id ユーザID
     * @return ユーザ情報
     * @throws UserNotFoundException ユーザが取得できない場合の例外
     */
    @Transactional
    public User getUserInfo(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User :" + id + " not found")
        );
    }

    /**
     * subjectに紐づくユーザ情報を取得します。
     *
     * @param subject ユーザ毎のid
     * @return ユーザ情報
     */
    public User findBySubject(String subject) throws UserNotFoundException {
        return userRepository.findBySubject(subject).orElse(null);
    }

    /**
     * ユーザが登録済みかどうかを判定します。
     *
     * @param user Idpにより提供されるユーザ情報
     * @return subjectに紐づくユーザが登録されている場合True, 未登録の場合false
     */
    public Boolean isRegistered(OidcUser user) {
        return userRepository.findBySubject(user.getAttribute("sub")).isPresent();
    }

    /**
     * ユーザのプロフィール、アーティスト、ブログ情報を取得します
     *
     * @param userId       ユーザID
     * @param isOthersInfo 他ユーザの情報を取得する場合true
     * @return ProfileRepositoryDto ユーザのプロフィール情報
     */
    public ProfileViewDto getUserProfile(Long userId, Boolean isOthersInfo) {
        User userInfo = userRepository.findById(userId).orElseThrow();
        List<Artist> favoriteArtistList = userArtistRepository.findFavoriteArtistsByUserId(userInfo.getId());
        List<Blog> createdBlogList = blogRepository.findPublishedBlogsByUserId(userInfo.getId());
        // 指定ユーザがフォロー中のユーザ数をカウント
        Long followedCount = followRepository.countFollowedUsers(userInfo.getId());
        // 指定ユーザのフォロワーのユーザ数をカウント
        Long followerCount = followRepository.countFollowers(userInfo.getId());
        // 指定ユーザのフォロー状況を取得（ログインユーザ自身の情報を取得する場合はnull）
        Boolean isFollow = null;
        if (isOthersInfo) {
            // ログインユーザの対象ユーザフォロー有無を取得
            isFollow = followRepository.isFollowing(userUtilService.getCurrentUser().getId(), userId);
        }
        return ProfileViewDto.build(userInfo, favoriteArtistList, createdBlogList, followedCount, followerCount, isFollow);
    }

    /**
     * キーワードに部分一致するユーザを検索する
     *
     * @param keyword 検索キーワード
     * @return 該当するユーザのリスト
     */
    public List<User> searchUser(String keyword) {
        return userRepository.searchUser(keyword, userUtilService.getCurrentUserId());
    }

    /**
     * ユーザを新規登録します。
     *
     * @param input Idpに紐づくユーザ情報
     * @return 登録されたユーザ情報
     */
    public User register(OidcUser input) {
        User user = User.builder()
                .displayName(input.getAttribute("given_name"))
                .subject(input.getAttribute("sub"))
                .enabled(true)
                .createdBy("System")
                .updatedBy("System")
                .build();
        return userRepository.save(user);
    }

    /**
     * ユーザを新規登録します。
     *
     * @param input ユーザ情報
     * @return 登録されたユーザ情報
     */
    public User register(User input) {
        return userRepository.save(input);
    }


    /**
     * ユーザ情報の初期更新を行います。
     *
     * @param input ユーザ更新情報
     */
    @Transactional
    public User initialUpdate(UserRegistrationForm input, MultipartFile profileImage) {
        return updateUser(input.getUserName(), null, profileImage, input.getArtistList());

    }


    /**
     * ユーザプロフィール情報を更新するメソッド
     */
    @Transactional
    public User updateUserProfile(UserUpdateForm input, MultipartFile profileImage) {
        return updateUser(input.getDisplayName(), input.getSelfIntroduction(), profileImage, input.getFavoriteArtistList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private User updateUser(String userName, String selfIntroduction, MultipartFile profileImage, List<Artist> artistList) {
        // 現在のファイルパスを取得
        String filePath = userUtilService.getCurrentUser().getProfileImageUrl();

        // リクエストにプロフィール画像が含まれている場合保存する
        if (profileImage != null && !profileImage.isEmpty()) {
            // ファイル名を一意にするためにUUIDを使用する（ユーザIDも利用可能）
            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();

            // ファイルの保存処理
            filePath = storageService.saveFile(profileImage, fileName);

        }
        // ユーザ情報の更新
        User currentUser = userUtilService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        userRepository.updateUserProfile(currentUser.getId(), userName, selfIntroduction, filePath);

        User updatedUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        // ユーザ、アーティストリレーション情報のリセット(リレーションの全削除)
        userArtistRepository.deleteAllByUserId(currentUser.getId());

        if (artistList != null && !artistList.isEmpty()) {
            // アーティスト情報、ユーザ、アーティストリレーション情報の登録
            for (Artist artist : artistList) {
                Artist registered = artistService.saveArtist(artist);

                UserArtist userArtist = UserArtist.builder()
                        .id(new UserArtistId())
                        .user(currentUser)
                        .artist(registered)
                        .createdBy(currentUser.getId().toString())
                        .updatedBy(currentUser.getId().toString())
                        .build();

                userArtistRepository.save(userArtist);
            }

        }
        entityManager.flush();
        entityManager.refresh(updatedUser);
        return updatedUser;

    }
}
