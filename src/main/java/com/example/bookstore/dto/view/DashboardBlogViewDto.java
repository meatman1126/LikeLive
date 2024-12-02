package com.example.bookstore.dto.view;

import com.example.bookstore.dto.repository.DashboardBlogRepositoryDto;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ダッシュボード用ブログViewDto
 */
@Data
@Builder
public class DashboardBlogViewDto {

    private Long id;
    private String blogTitle;
    /**
     * サムネイル画像
     */
    private String thumbnailUrl;
    private String profileImageUrl;
    private String authorName;
    private Boolean isFollowAuthor;
    private String blogCreatedTime;

    private static DashboardBlogViewDto build(DashboardBlogRepositoryDto repositoryDto) {
        return DashboardBlogViewDto.builder()
                .id(repositoryDto.getId())
                .blogTitle(repositoryDto.getBlogTitle())
                .thumbnailUrl(repositoryDto.getThumbnailUrl())
                .profileImageUrl(repositoryDto.getProfileImageUrl())
                .authorName(repositoryDto.getAuthorName())
                .isFollowAuthor(repositoryDto.getIsFollowAuthor())
                .blogCreatedTime(repositoryDto.getBlogCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static List<DashboardBlogViewDto> toViewDto(List<DashboardBlogRepositoryDto> repositoryDtoList) {
        List<DashboardBlogViewDto> dashboardViewDtoList = new ArrayList<>();
        for (DashboardBlogRepositoryDto repositoryDto : repositoryDtoList) {
            dashboardViewDtoList.add(DashboardBlogViewDto.build(repositoryDto));
        }

        return dashboardViewDtoList;
    }
}
