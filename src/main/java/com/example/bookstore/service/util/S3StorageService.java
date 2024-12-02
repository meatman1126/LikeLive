package com.example.bookstore.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class S3StorageService implements StorageService {

    private static final String UPLOADS_FOLDER = "uploads/";
    private final S3Client s3Client;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String saveFile(MultipartFile file, String fileName) {
        try {
            String key = UPLOADS_FOLDER + fileName;

            // MultipartFileを一時ファイルに変換
            Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());

            // S3にファイルをアップロード
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl(ObjectCannedACL.PRIVATE) // アクセス制御を指定
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

            // アップロード後に一時ファイルを削除
            Files.delete(tempFile);

            // アップロードされたファイルのキーを返す
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("S3にファイルを保存中にエラーが発生しました", e);
        }
    }

    @Override
    public Resource getFile(String fileName) {
        String key = UPLOADS_FOLDER + fileName;
        try {
            // S3からファイルを取得
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(responseInputStream);
        } catch (S3Exception e) {
            throw new RuntimeException("S3からファイルを取得中にエラーが発生しました", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        String key = UPLOADS_FOLDER + fileName;
        // ファイルを削除するためのリクエストを作成
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // S3からファイルを削除
        s3Client.deleteObject(deleteObjectRequest);
    }
}