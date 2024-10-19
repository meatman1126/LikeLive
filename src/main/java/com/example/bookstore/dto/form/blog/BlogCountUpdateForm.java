package com.example.bookstore.dto.form.blog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogCountUpdateForm {

    private Long id;
    private Boolean isCancel;
}
