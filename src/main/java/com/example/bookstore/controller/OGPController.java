package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.bookstore.entity.Blog;
import com.example.bookstore.exception.BlogNotFoundException;
import com.example.bookstore.service.BlogService;

@Controller
public class OGPController {

    @Autowired
    BlogService blogService;

    @GetMapping("/meta/blog/{blogId}")
    public String getBlogMeta(@PathVariable Long blogId, Model model, @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        try {
            Blog blog = blogService.findById(blogId);
            model.addAttribute("title", blog.getTitle() + " - LikeLive");
            model.addAttribute("description", "ブログを開く");
            model.addAttribute("imageUrl", "https://d3n24a5r1zq3yp.cloudfront.net/uploads/" + blog.getThumbnailUrl());
            model.addAttribute("url", "https://likelive.xyz/blog/view/" + blogId);
        } catch (BlogNotFoundException e) {
            // ブログが見つからない場合はデフォルトのメタデータを設定
            model.addAttribute("title", "LikeLive - 音楽好きが集うSNS");
            model.addAttribute("description", "LikeLiveで音楽の感想やライブ体験をシェアしよう");
            model.addAttribute("imageUrl", "https://likelive.xyz/static/media/top.dd8d7f5753465248b8de.png");
            model.addAttribute("url", "https://likelive.xyz/");
        }

        return "blogMeta";
    }
}