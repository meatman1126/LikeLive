package com.example.bookstore.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.example.bookstore.SpringBootTestBase;
import com.example.bookstore.TestDataSetup;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.jpa.UserRepository;

@SpringBootTest @ActiveProfiles("test")
class UserUtilServiceTest extends SpringBootTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserUtilService userUtilService;

    @Autowired
    private TestDataSetup testDataSetup;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = testDataSetup.createTestUser();
    }

    @Test @DisplayName("認証済みユーザーの情報を正しく取得できる")
    void getCurrentUser_WhenAuthenticated_ReturnsUser() {
        // 認証情報の設定
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser.getSubject());
        SecurityContextHolder.setContext(securityContext);

        // ユーザー情報の設定
        when(userRepository.findBySubject(testUser.getSubject())).thenReturn(java.util.Optional.of(testUser));

        // テスト実行
        User result = userUtilService.getCurrentUser();

        // 検証
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getSubject(), result.getSubject());
        assertEquals(testUser.getDisplayName(), result.getDisplayName());
    }

    @Test @DisplayName("未認証の場合、例外がスローされる")
    void getCurrentUser_WhenNotAuthenticated_ThrowsException() {
        // 認証情報の設定
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // テスト実行と検証
        assertThrows(RuntimeException.class, () -> userUtilService.getCurrentUser());
    }

    @Test @DisplayName("認証情報が存在しない場合、例外がスローされる")
    void getCurrentUser_WhenNoAuthentication_ThrowsException() {
        // 認証情報をクリア
        SecurityContextHolder.clearContext();

        // テスト実行と検証
        assertThrows(RuntimeException.class, () -> userUtilService.getCurrentUser());
    }

    @Test @DisplayName("ユーザーが見つからない場合、nullが返される")
    void getCurrentUser_WhenUserNotFound_ReturnsNull() {
        // 認証情報の設定
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("non-existent-subject");
        SecurityContextHolder.setContext(securityContext);

        // ユーザー情報の設定
        when(userRepository.findBySubject("non-existent-subject")).thenReturn(java.util.Optional.empty());

        // テスト実行
        User result = userUtilService.getCurrentUser();

        // 検証
        assertNull(result);
    }

    @Test @DisplayName("現在のユーザーIDを文字列として取得できる")
    void getCurrentUserId_ReturnsUserIdAsString() {
        // 認証情報の設定
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser.getSubject());
        SecurityContextHolder.setContext(securityContext);

        // ユーザー情報の設定
        when(userRepository.findBySubject(testUser.getSubject())).thenReturn(java.util.Optional.of(testUser));

        // テスト実行
        String result = userUtilService.getCurrentUserId();

        // 検証
        assertEquals(testUser.getId().toString(), result);
    }
}