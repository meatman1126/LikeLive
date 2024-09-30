package com.example.bookstore.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LocalStorageService implements StorageService {

    @Value("${storage.local.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String fileName) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString(); // ローカルパスを返す
        } catch (IOException e) {
            throw new RuntimeException("ファイル保存中にエラーが発生しました", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("ファイル削除中にエラーが発生しました", e);
        }
    }
}