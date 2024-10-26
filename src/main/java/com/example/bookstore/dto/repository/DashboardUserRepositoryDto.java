package com.example.bookstore.dto.repository;

import com.example.bookstore.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * ダッシュボード用ユーザRepositoryDto
 */
@Data
@Builder
public class DashboardUserRepositoryDto {
    /**
     * ユーザ情報
     */
    private User user;

    /**
     * フォロー状況（ログインユーザが対象ユーザをフォローしている場合true）
     */
    private boolean isFollowing;
}
