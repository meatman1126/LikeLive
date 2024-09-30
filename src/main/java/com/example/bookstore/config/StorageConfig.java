package com.example.bookstore.config;

import com.example.bookstore.service.util.LocalStorageService;
import com.example.bookstore.service.util.S3StorageService;
import com.example.bookstore.service.util.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {

    @Value("${aws.region}")
    private String region;


    //     S3ClientのBeanを作成
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build(); // 必要に応じてカスタマイズ（リージョン設定など）
    }

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "local")
    public StorageService localStorageService() {
        return new LocalStorageService();
    }

    @Bean
    @ConditionalOnProperty(name = "storage.type", havingValue = "s3")
    public StorageService s3StorageService(S3Client s3Client) {
        return new S3StorageService(s3Client);  // S3Clientを注入
    }
}