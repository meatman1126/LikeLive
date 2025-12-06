package com.example.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * JPA関連のテストケース用の基底クラス
 * 
 * このクラスは以下の機能を提供します：
 * - JPA関連のテスト設定（@DataJpaTest）
 * - エンティティマネージャーの自動構成
 * - インメモリデータベースの設定
 * - テストデータセットアップユーティリティ
 */
@DataJpaTest
@Import(TestDataSetup.class)
@ActiveProfiles("test")
public abstract class JpaTestBase extends TestBase {
    @Autowired
    protected TestDataSetup testDataSetup;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // 将来的にJPA関連の共通ユーティリティメソッドをここに追加可能
}