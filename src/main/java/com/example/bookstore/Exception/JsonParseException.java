package com.example.bookstore.exception;

/**
 * JSONパースエラーを表す例外クラス
 */
public class JsonParseException extends RuntimeException {
    
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
} 