package com.example.bookstore.service.util;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveFile(MultipartFile file, String fileName);

    void deleteFile(String fileName);
}
