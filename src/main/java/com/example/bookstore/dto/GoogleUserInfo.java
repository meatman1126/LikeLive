package com.example.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * GoogleAPIから取得したユーザ情報を受け取るためのDTOクラスです。
 */
@Data
public class GoogleUserInfo {
    private String sub;
    private String name;

    @JsonProperty("given_name")
    private String givenName;
    private String email;
    private String picture;

}
