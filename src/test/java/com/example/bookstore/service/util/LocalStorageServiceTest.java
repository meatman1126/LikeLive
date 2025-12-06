package com.example.bookstore.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.example.bookstore.SpringBootTestBase;

@SpringBootTest @ActiveProfiles("test")
class LocalStorageServiceTest extends SpringBootTestBase {

    @Autowired
    private LocalStorageService localStorageService;

    @TempDir
    Path tempDir;

    private MockMultipartFile testFile;
    private String testFileName;

    @BeforeEach
    void setUp() {
        // テスト用のファイルを作成
        String content = "Test file content";
        testFileName = "test-" + UUID.randomUUID().toString() + ".txt";
        testFile = new MockMultipartFile("file", testFileName, "text/plain", content.getBytes());
    }

    @AfterEach
    void tearDown() {
        // テストファイルの削除
        try {
            localStorageService.deleteFile(testFileName);
        } catch (Exception e) {
            // ファイルが存在しない場合は無視
        }
    }

    @Test @DisplayName("ファイルを保存して取得できる")
    void saveAndGetFile() throws IOException {
        // ファイルの保存
        String savedFileName = localStorageService.saveFile(testFile, testFileName);
        assertEquals(testFileName, savedFileName);

        // ファイルの取得
        Resource resource = localStorageService.getFile(testFileName);
        assertNotNull(resource);
        assertTrue(resource.exists());

        // ファイルの内容を検証
        String content = new String(resource.getInputStream().readAllBytes());
        assertEquals("Test file content", content);
    }

    @Test @DisplayName("ファイルを削除できる")
    void deleteFile() throws IOException {
        // ファイルの保存
        localStorageService.saveFile(testFile, testFileName);

        // ファイルの削除
        localStorageService.deleteFile(testFileName);

        // ファイルが削除されたことを確認（例外が発生することを検証）
        IOException exception = assertThrows(IOException.class, () -> {
            localStorageService.getFile(testFileName);
        });
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test @DisplayName("存在しないファイルを取得しようとすると例外がスローされる")
    void getNonExistentFile_ThrowsException() {
        assertThrows(IOException.class, () -> {
            localStorageService.getFile("non-existent-file.txt");
        });
    }
}