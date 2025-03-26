package com.example.bookstore.exception;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

/**
 * グローバル例外ハンドラ
 * アプリケーション全体で発生する例外を適切なHTTPレスポンスに変換します。
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * リソースが存在しない場合のエラーハンドリング
     * HTTP 404 Not Foundを返却します。
     * 
     * @param ex リソースが見つからない場合の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（404 Not Found）
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * アクセス権限がない場合のエラーハンドリング
     * HTTP 403 Forbiddenを返却します。
     * 
     * @param ex アクセス権限がない場合の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（403 Forbidden）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Forbidden")
            .message("You don't have permission to access this resource")
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * 認証失敗時のエラーハンドリング
     * HTTP 401 Unauthorizedを返却します。
     * 
     * @param ex 認証失敗時の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（401 Unauthorized）
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message("Authentication failed")
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * データベースの制約違反エラーハンドリング
     * HTTP 400 Bad Requestを返却します。
     * 
     * @param ex データベース制約違反の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（400 Bad Request）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * パラメータの型変換エラーハンドリング
     * HTTP 400 Bad Requestを返却します。
     * 
     * @param ex パラメータの型変換エラーの例外
     * @param request リクエスト情報
     * @return エラーレスポンス（400 Bad Request）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Argument Type")
            .message(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()))
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * JSONパースエラーのハンドリング
     * HTTP 400 Bad Requestを返却します。
     * 
     * @param ex JSONパースエラーの例外
     * @param request リクエスト情報
     * @return エラーレスポンス（400 Bad Request）
     */
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(
            JsonParseException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid JSON Format")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * ファイルが見つからない場合のエラーハンドリング
     * HTTP 404 Not Foundを返却します。
     * 
     * @param ex ファイルが見つからない場合の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（404 Not Found）
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(
            FileNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("File Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * ファイルへのアクセス権限がない場合のエラーハンドリング
     * HTTP 403 Forbiddenを返却します。
     * 
     * @param ex ファイルへのアクセス権限がない場合の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（403 Forbidden）
     */
    @ExceptionHandler(FileAccessException.class)
    public ResponseEntity<ErrorResponse> handleFileAccessException(
            FileAccessException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("File Access Denied")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * その他の未処理の例外のハンドリング
     * HTTP 500 Internal Server Errorを返却します。
     * 
     * @param ex 未処理の例外
     * @param request リクエスト情報
     * @return エラーレスポンス（500 Internal Server Error）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getDescription(false))
            .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 