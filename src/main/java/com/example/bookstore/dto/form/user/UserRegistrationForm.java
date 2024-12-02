package com.example.bookstore.dto.form.user;

import com.example.bookstore.entity.Artist;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRegistrationForm {
    private String userName;

    private String profileImagePath;

    private List<Artist> artistList;
}
