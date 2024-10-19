package com.example.bookstore.dto.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

/**
 * JPA用のコンバータクラス。
 * オブジェクトをJSON文字列としてデータベースに保存しデータベースから読み取ったJSON文字列をオブジェクトとしてエンティティにマッピングする。
 */
@Converter
public class ContentConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * オブジェクトをJSON文字列に変換し、データベースに保存する。
     *
     * @param attribute エンティティの属性値。変換対象のオブジェクト。
     * @return JSON文字列。属性がnullの場合、nullを返す。
     * @throws IllegalArgumentException JSON変換に失敗した場合にスローされる。
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting content to JSON string", e);
        }
    }

    /**
     * データベースのJSON文字列をオブジェクトに変換し、エンティティ属性にマッピングする。
     *
     * @param dbData データベースから取得したJSON文字列。
     * @return オブジェクト。データがnullまたは空文字列の場合はnullを返す。
     * @throws IllegalArgumentException JSON文字列の読み込みに失敗した場合にスローされる。
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON string to content", e);
        }
    }
}