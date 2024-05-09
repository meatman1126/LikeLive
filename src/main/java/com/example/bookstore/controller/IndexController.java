package com.example.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("message", "Hello World");
        System.out.println("呼ばれました。");
        return "hello";
    }
    @GetMapping("/hi")
    public String hi() {
        return "hi";
    }
}
