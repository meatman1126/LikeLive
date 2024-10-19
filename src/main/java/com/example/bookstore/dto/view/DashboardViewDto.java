package com.example.bookstore.dto.view;

import com.example.bookstore.dto.repository.DashboardRepositoryDto;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DashboardViewDto {

    private Long id;
    private String blogTitle;
    private String profileImageUrl;
    private String authorName;
    private Boolean isFollowAuthor;
    private String blogCreatedTime;

    private static DashboardViewDto build(DashboardRepositoryDto repositoryDto) {
        return DashboardViewDto.builder()
                .id(repositoryDto.getId())
                .blogTitle(repositoryDto.getBlogTitle())
                .profileImageUrl(repositoryDto.getProfileImageUrl())
                .authorName(repositoryDto.getAuthorName())
                .isFollowAuthor(repositoryDto.getIsFollowAuthor())
                .blogCreatedTime(repositoryDto.getBlogCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public static List<DashboardViewDto> toViewDto(List<DashboardRepositoryDto> repositoryDtoList) {
        List<DashboardViewDto> dashboardViewDtoList = new ArrayList<>();
        for (DashboardRepositoryDto repositoryDto : repositoryDtoList) {
            dashboardViewDtoList.add(DashboardViewDto.build(repositoryDto));
        }

        return dashboardViewDtoList;
    }
}
