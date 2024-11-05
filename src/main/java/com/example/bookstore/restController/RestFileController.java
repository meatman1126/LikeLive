package com.example.bookstore.restController;

import com.example.bookstore.service.util.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Restファイルコントローラ
 */
@RestController
@RequestMapping("/api")
public class RestFileController {

    /**
     * ストレージサービス
     */
    @Autowired
    StorageService storageService;

    /**
     * 指定されたファイルを取得します。
     *
     * @param filename ファイル名
     * @return ファイル
     */
    @GetMapping("/public/files/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource resource = storageService.getFile(filename);

            // ファイルが存在し、アクセス可能か確認
            if (resource.exists() || resource.isReadable()) {
                // レスポンスにファイルを含めて返却
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("ファイルが存在しないか、読み込み不可能です。");
            }
        } catch (Exception e) {
            throw new RuntimeException("ファイルの取得中にエラーが発生しました。", e);
        }
    }

    /**
     * ファイルを保存します。
     *
     * @param file 保存対象のファイル
     * @return 保存したファイル名
     */
    @PostMapping("/file/save")
    public ResponseEntity<String> saveFile(@RequestParam(value = "file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("保存対象のファイルが存在しません。");
            }
            // ファイル名を一意にするためにUUIDを使用する（ユーザIDも利用可能）
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // ファイルの保存処理
            storageService.saveFile(file, fileName);

            return ResponseEntity.ok(fileName);

        } catch (Exception e) {
            throw new RuntimeException("ファイルの取得中にエラーが発生しました。", e);
        }
    }
}