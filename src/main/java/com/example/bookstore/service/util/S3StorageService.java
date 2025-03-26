package com.example.bookstore.service.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

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
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
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
    public Resource getFile(String fileName) throws MalformedURLException, IOException {
        String key = UPLOADS_FOLDER + fileName;
        try {
            // ファイルの存在を確認
            try {
                HeadObjectRequest headRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
                s3Client.headObject(headRequest);
            } catch (NoSuchKeyException e) {
                throw new IOException("File not found: " + fileName);
            }

            // S3からファイルを取得
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(responseInputStream);
        } catch (S3Exception e) {
            if (e instanceof NoSuchKeyException) {
                throw new IOException("File not found: " + fileName);
            }
            throw new RuntimeException("S3からファイルを取得中にエラーが発生しました", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        String key = UPLOADS_FOLDER + fileName;
        // ファイルを削除するためのリクエストを作成
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();

        // S3からファイルを削除
        s3Client.deleteObject(deleteObjectRequest);
    }
}