package com.example.bookstore.dto.view;

import com.example.bookstore.dto.repository.FollowRepositoryDto;
import com.example.bookstore.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * フォローViewDTO
 */
@Data
@Builder
public class FollowViewDto {


    /**
     * ユーザ情報（フォロー対象、フォロワー相互の役割がある）
     */
    private User user;
    /**
     * フォロー状態（userをログインユーザがフォローしているかを保持）
     */
    private Boolean isFollowing;

    /**
     * RepositoryDtoからViewDtoに変換します。
     *
     * @param repositoryDtoList RepositoryDtoのリスト
     * @return 変換したViewDtoのリスト
     */
    public static List<FollowViewDto> build(List<FollowRepositoryDto> repositoryDtoList) {
        List<FollowViewDto> viewDtoList = new ArrayList<>();
        for (FollowRepositoryDto repositoryDto : repositoryDtoList) {
            FollowViewDto viewDto = FollowViewDto.builder()
                    .user(repositoryDto.getUser())
                    .isFollowing(repositoryDto.getIsFollowing())
                    .build();
            viewDtoList.add(viewDto);
        }
        return viewDtoList;

    }

}
