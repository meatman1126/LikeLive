package com.example.bookstore.service.util;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface StorageService {
    String saveFile(MultipartFile file, String fileName);

    void deleteFile(String fileName);

    Resource getFile(String fileName) throws MalformedURLException;
}
