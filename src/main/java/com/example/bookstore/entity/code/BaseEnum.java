package com.example.bookstore.entity.code;

public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> {
    static <T extends Enum<T> & BaseEnum<T>> T fromCode(Class<T> enumType, int code) {
        for (T enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.getCode() == code) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    int getCode();
}
