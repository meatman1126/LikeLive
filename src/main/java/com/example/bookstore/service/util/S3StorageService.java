package com.example.bookstore.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String saveFile(MultipartFile file, String fileName) {
        try {
            // MultipartFileを一時ファイルに変換
            Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());

            // S3にファイルをアップロード
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

            // アップロード後に一時ファイルを削除
            Files.delete(tempFile);

            // アップロードされたファイルのURLを返す
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        } catch (IOException e) {
            throw new RuntimeException("S3にファイルを保存中にエラーが発生しました", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        // ファイルを削除するためのリクエストを作成
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // S3からファイルを削除
        s3Client.deleteObject(deleteObjectRequest);
    }
}