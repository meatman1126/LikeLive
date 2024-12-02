package com.example.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")  // テスト用プロファイルを指定
class BookstoreApplicationTests {

    @Test
    void contextLoads() {
    }

}
