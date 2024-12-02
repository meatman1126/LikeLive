package com.example.bookstore.dto.view;


import com.example.bookstore.dto.repository.DashboardUserRepositoryDto;
import com.example.bookstore.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ダッシュボード用ユーザViewDto
 */
@Data
@Builder
public class DashboardUserViewDto {
    /**
     * ユーザ情報
     */
    private User user;
    
    /**
     * フォロー状況（ログインユーザが対象ユーザをフォローしている場合true）
     */
    private boolean isFollowing;

    public static List<DashboardUserViewDto> build(List<DashboardUserRepositoryDto> repositoryDtoList) {
        List<DashboardUserViewDto> viewDtoList = new ArrayList<>();
        for (DashboardUserRepositoryDto repositoryDto : repositoryDtoList) {
            viewDtoList.add(build(repositoryDto));
        }
        return viewDtoList;
    }

    private static DashboardUserViewDto build(DashboardUserRepositoryDto repositoryDto) {
        return DashboardUserViewDto.builder()
                .user(repositoryDto.getUser())
                .isFollowing(repositoryDto.isFollowing())
                .build();
    }
}
