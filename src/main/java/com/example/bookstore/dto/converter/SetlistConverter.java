package com.example.bookstore.dto.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.LinkedHashMap;

@Converter
public class SetlistConverter implements AttributeConverter<LinkedHashMap<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LinkedHashMap<String, String> setlist) {
        try {
            return objectMapper.writeValueAsString(setlist);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting LinkedHashMap to JSON", e);
        }
    }

    @Override
    public LinkedHashMap<String, String> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, String>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to LinkedHashMap", e);
        }
    }
}