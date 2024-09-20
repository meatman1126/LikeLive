package com.example.bookstore.dto.form.user;

import com.example.bookstore.entity.Artist;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRegistrationForm {
    private String userName;

    private List<Artist> artistList;

    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "userName='" + userName + '\'' +
                '}';
    }

//    @Data
//    public static class Artist {
//        private String id;
//        private String name;
//
//        private String imageUrl;
//    }

}
