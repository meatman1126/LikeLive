package com.example.bookstore.exception;

/**
 * ファイルへのアクセス権限がない場合の例外
 */
public class FileAccessException extends RuntimeException {
    
    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
} 