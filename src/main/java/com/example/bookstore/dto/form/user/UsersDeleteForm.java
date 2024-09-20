package com.example.bookstore.dto.form.user;

import lombok.Data;

import java.util.List;

@Data
public class UsersDeleteForm {
    private List<Long> idList;
}
