package com.example.bookstore.dto.converter;

import com.example.bookstore.entity.Setlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * SetlistエンティティとJSON文字列間の変換を行うコンバータクラス。
 *
 * <p>
 * JPAのAttributeConverterを実装し、Setlistオブジェクトをデータベースに保存する際に
 * JSON文字列に変換し、データベースから読み出す際にJSON文字列をSetlistオブジェクトに変換します。
 * </p>
 */
@Converter(autoApply = true)
public class SetlistConverter implements AttributeConverter<Setlist, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SetlistオブジェクトをJSON文字列に変換してデータベースに保存します。
     *
     * @param setList セットリストオブジェクト
     * @return JSON文字列。セットリストがnullの場合はnullを返します。
     */
    @Override
    public String convertToDatabaseColumn(Setlist setList) {
        try {
            if (setList == null) {
                return null;
            }
            // JavaオブジェクトをJSON文字列に変換
            return objectMapper.writeValueAsString(setList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("SetListのシリアライズに失敗しました", e);
        }
    }

    /**
     * データベースのJSON文字列をSetlistオブジェクトに変換します。
     *
     * @param dbData データベースから取得したJSON文字列
     * @return Setlistオブジェクト。データがnullまたは空の場合はnullを返します。
     */
    @Override
    public Setlist convertToEntityAttribute(String dbData) {
        try {

            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            // JSON文字列をJavaオブジェクトに変換
            return objectMapper.readValue(dbData, Setlist.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("SetListのデシリアライズに失敗しました", e);
        }
    }
}