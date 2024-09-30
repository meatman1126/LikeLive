package com.example.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {


    @RequestMapping("{path:^(?!.*static).*$}/**")
    public String all() {
        return "/index";
    }
}
