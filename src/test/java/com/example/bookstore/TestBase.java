package com.example.bookstore;

import org.junit.jupiter.api.AfterEach;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.bookstore.entity.User;

/**
 * すべてのテストケース用の基底クラス
 * Spring特有の機能に依存しない共通機能をここに定義します
 */
public abstract class TestBase {
    protected User testUser;

    /**
     * テストユーザーを作成し、認証情報を設定します。
     * 認証が必要なテストケースで使用してください。
     * @param testDataSetup テストデータセットアップユーティリティ
     * @return 作成されたテストユーザー
     */
    protected User createUserAndSetupAuthentication(TestDataSetup testDataSetup) {
        // テストユーザーを作成
        testUser = testDataSetup.createTestUser();
        
        // 認証情報を設定
        TestingAuthenticationToken authentication = 
            new TestingAuthenticationToken(testUser.getSubject(), null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return testUser;
    }

    @AfterEach
    void tearDown() {
        // テスト後に認証情報をクリア
        SecurityContextHolder.clearContext();
    }
} 