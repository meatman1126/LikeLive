package com.example.bookstore.service;

import com.example.bookstore.Exception.UserNotFoundException;
import com.example.bookstore.dto.form.user.UserRegistrationForm;
import com.example.bookstore.entity.Artist;
import com.example.bookstore.entity.User;
import com.example.bookstore.entity.UserArtist;
import com.example.bookstore.entity.key.UserArtistId;
import com.example.bookstore.repository.jpa.UserArtistRepository;
import com.example.bookstore.repository.jpa.UserRepository;
import com.example.bookstore.service.util.StorageService;
import com.example.bookstore.service.util.UserUtilService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserUtilService userUtilService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistService artistService;

    @Autowired
    UserArtistRepository userArtistRepository;

    @Autowired
    StorageService storageService;


    /**
     * 指定されたユーザ情報を取得します。
     *
     * @param id ユーザID
     * @return ユーザ情報
     * @throws UserNotFoundException ユーザが取得できない場合の例外
     */
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
     * @return 更新後のユーザ情報
     */
    @Transactional
    public User initialUpdate(UserRegistrationForm input, MultipartFile profileImage) {

        String filePath = null;
        // プロフィール画像の保存処理
        if (profileImage != null && !profileImage.isEmpty()) {
            // ファイル名を一意にするためにUUIDを使用する（ユーザIDも利用可能）
            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();

            // ファイルの保存処理
            filePath = storageService.saveFile(profileImage, fileName);

            // 保存したファイルのパスを更新情報にセット
            input.setProfileImagePath(filePath);
        }
        // ユーザ情報の更新
        User currentUser = userUtilService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        userRepository.updateProfile(currentUser.getId(), input.getUserName(), filePath, now);

        User updatedUser = userRepository.findById(currentUser.getId()).orElse(currentUser);

        if (input.getArtistList() != null && !input.getArtistList().isEmpty()) {
            // アーティスト情報、ユーザ、アーティストリレーション情報の登録
            for (Artist artist : input.getArtistList()) {
                Artist registered = artistService.saveArtist(artist);
                UserArtist userArtist = UserArtist.builder()
                        .id(new UserArtistId())
                        .user(updatedUser)
                        .artist(registered)
                        .createdBy(updatedUser.getId().toString())
                        .updatedBy(updatedUser.getId().toString())
                        .build();
                userArtistRepository.save(userArtist);
            }

        }
        return updatedUser;
    }


    /**
     * ユーザプロフィール情報を更新するメソッド
     *
     * @param displayName      新しい表示名
     * @param selfIntroduction 新しい自己紹介文
     * @param profileImageUrl  新しい画像URL
     */
    @Transactional
    public User updateUserProfile(String displayName, String selfIntroduction, String profileImageUrl) {
        userRepository.updateUserProfile(userUtilService.getCurrentUser().getId(), displayName, selfIntroduction, profileImageUrl);
        return userRepository.findById(userUtilService.getCurrentUser().getId()).orElseThrow();
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}
