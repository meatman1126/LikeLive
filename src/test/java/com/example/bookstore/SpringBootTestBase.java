package com.example.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Bootテスト用の基底クラス
 * Spring Boot特有の機能を使用するテストケースで使用します
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestDataSetup.class)
public abstract class SpringBootTestBase extends TestBase {
    @Autowired
    protected TestDataSetup testDataSetup;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // Spring Boot特有の共通機能をここに定義
} 