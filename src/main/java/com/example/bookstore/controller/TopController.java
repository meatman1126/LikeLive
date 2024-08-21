package com.example.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    @GetMapping("/top")
    public String hello(Model model) {
        model.addAttribute("message", "Hello World");
        return "top";
    }
}
