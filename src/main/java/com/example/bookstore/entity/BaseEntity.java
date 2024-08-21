package com.example.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity {
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.createdBy = getCurrentUsername();
        this.updatedAt = now;
        this.updatedBy = getCurrentUsername();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = getCurrentUsername();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // ログインユーザーのユーザー名を取得
        }
        return "System"; // 認証されていない場合のデフォルト値
    }
}
