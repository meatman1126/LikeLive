package com.example.bookstore.service.util;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveFile(MultipartFile file, String fileName);

    void deleteFile(String fileName);

    Resource getFile(String fileName) throws MalformedURLException, IOException;
}
