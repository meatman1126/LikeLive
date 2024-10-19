package com.example.bookstore.dto.repository;

import com.example.bookstore.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * フォローリポジトリDTO
 */
@Data
@Builder
@AllArgsConstructor
public class FollowRepositoryDto {

    /**
     * ユーザ情報（フォロー対象、フォロワー相互の役割がある）
     */
    private User user;
    /**
     * フォロー状態（userをログインユーザがフォローしているかを保持）
     */
    private Boolean isFollowing;
}
